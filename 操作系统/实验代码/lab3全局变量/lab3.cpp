#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <sys/shm.h>
#include <sys/wait.h>
#include <sys/sem.h>
#include <sys/ipc.h>
#include <sys/types.h>
#include <errno.h>
#include <assert.h>
void get(void);
void put(void);
//int end = 0;	   //get结束的标志，用来控制put的结束
//int end_index = 0; //get结束对应的缓冲区的下标

#define NMAX 8				  //缓冲区个数
const size_t buf_size = 2048; //单个缓冲区的大小
unsigned char *buf[8];
int buf_index = 0; //缓冲区下标

union semun
{
	int val;
	struct semid_ds *buf;
	unsigned short *array;
	struct seminfo *__buf;
};

union semun semopts;
//全局变量的声明
int get_id = 0, put_id = 0; //进程号
FILE *out;
FILE *in;
int buf_id0 = 0, buf_id1 = 0, buf_id2 = 0, buf_id3 = 0, buf_id4 = 0, buf_id5 = 0;
int buf_id6 = 0, buf_id7 = 0;
//创建8个缓冲区
//int buf_empty = 0, buf_max = 0, buf_mutex = 0, over = 0;
int file_read = 0, file_write = 0, over = 0;
//信号灯
int len; //输入文件的长度
void P(int semid, int index)
{
	struct sembuf sem;
	sem.sem_num = index;
	sem.sem_op = -1;
	sem.sem_flg = 0;
	semop(semid, &sem, 1);
	return;
}

void V(int semid, int index)
{
	struct sembuf sem;
	sem.sem_num = index;   //将要处理的信号灯的下标
	sem.sem_op = 1;		   //要执行的操作
	sem.sem_flg = 0;	   ////操作标记：0或IPC_NOWAIT等
	semop(semid, &sem, 1); //1:表示执行命令的个数
	return;
}

int main(int argc, char *argv[])
{
	//定义共享缓冲区及其信号灯
	//IPC_PRIVATE创建的IPC对象, key值属性为0，和IPC对象的编号就没有了对应关系。
	//这样毫无关系的进程，就不能通过key值来得到IPC对象的编号（因为这种方式创建的IPC对象的key值都是0）
	//。因此，这种方式产生的IPC对象，和无名管道类似，不能用于毫无关系的进程间通信。
	//创建共享缓冲区
	buf_id0 = shmget(IPC_PRIVATE, buf_size, IPC_CREAT | 0666);
	buf_id1 = shmget(IPC_PRIVATE, buf_size, IPC_CREAT | 0666);
	buf_id2 = shmget(IPC_PRIVATE, buf_size, IPC_CREAT | 0666);
	buf_id3 = shmget(IPC_PRIVATE, buf_size, IPC_CREAT | 0666);
	buf_id4 = shmget(IPC_PRIVATE, buf_size, IPC_CREAT | 0666);
	buf_id5 = shmget(IPC_PRIVATE, buf_size, IPC_CREAT | 0666);
	buf_id6 = shmget(IPC_PRIVATE, buf_size, IPC_CREAT | 0666);
	buf_id7 = shmget(IPC_PRIVATE, buf_size, IPC_CREAT | 0666);
	//共享缓冲区附接
	buf[0] = (unsigned char *)shmat(buf_id0, 0, 0);
	buf[1] = (unsigned char *)shmat(buf_id1, 0, 0);
	buf[2] = (unsigned char *)shmat(buf_id2, 0, 0);
	buf[3] = (unsigned char *)shmat(buf_id3, 0, 0);
	buf[4] = (unsigned char *)shmat(buf_id4, 0, 0);
	buf[5] = (unsigned char *)shmat(buf_id5, 0, 0);
	buf[6] = (unsigned char *)shmat(buf_id4, 0, 0);
	buf[7] = (unsigned char *)shmat(buf_id5, 0, 0);
	//创建信号量
	file_read = semget(IPC_PRIVATE, 1, IPC_CREAT | 0666);
	file_write = semget(IPC_PRIVATE, 1, IPC_CREAT | 0666);
	over = semget(IPC_PRIVATE, 1, IPC_CREAT | 0666);
	//buf_mutex = semget(IPC_PRIVATE, 1, IPC_CREAT | 0666);
	//over = semget(IPC_PRIVATE, 1, IPC_CREAT | 0666);
	if (file_read < 0 || file_write < 0)
	{
		printf("semget error!!! \n");
		printf("errno is: %d\n", errno);
		exit(1);
	}
	//信号灯赋值
	semopts.val = 8;
	semctl(file_read, 0, SETVAL, semopts);

	semopts.val = 0;
	semctl(file_write, 0, SETVAL, semopts);
	//semctl(part_over, 0, SETVAL, semopts);
	semctl(over, 0, SETVAL, semopts);

	//文件处理
	if (argc != 3)
	{
		printf("arguments error\n");
		return 0;
	}
	if ((in = fopen(argv[1], "rb")) == NULL)
	{
		//文件打开失败不需要关闭文件
		printf("can't open input file!\n");
		return 0;
	}
	if ((out = fopen(argv[2], "wb")) == NULL)
	{
		printf("can't open output file!\n");
		fclose(in);
		return 0;
	}
	fseek(in, 0, SEEK_END);
	len = ftell(in);
	fseek(in, 0, SEEK_SET);

	assert((get_id = fork()) >= 0);
	if (get_id == 0)
	{
		get();
		exit(0);
		/*if (get_id == 0) {
			puts("get created\n");
			execv("./get",NULL);
		}*/
	}
	else
	{
		assert((put_id = fork()) >= 0);
		if (put_id == 0)
		{
			put();
			exit(0);
		}
	}
	//printf("[主进程debug]主进程等待子进程结束\n");
	//等待进程结束
	waitpid(get_id, NULL, 0);
	waitpid(put_id, NULL, 0);
	//printf("[主进程debug]主进程开始\n");
	//缓冲区断开
	shmdt(buf[0]);
	shmdt(buf[1]);
	shmdt(buf[2]);
	shmdt(buf[3]);
	shmdt(buf[4]);
	shmdt(buf[5]);
	shmdt(buf[6]);
	shmdt(buf[7]);
	printf("共享存储区断开\n");
	//删除信号灯
	semctl(file_read, 0, IPC_RMID, 0);
	semctl(file_write, 0, IPC_RMID, 0);
	//semctl(buf_mutex, 0, IPC_RMID, 0);
	//semctl(part_over, 0, IPC_RMID, 0);
	semctl(over, 0, IPC_RMID, 0);
	//删除共享内存组
	shmctl(buf_id0, IPC_RMID, 0);
	shmctl(buf_id1, IPC_RMID, 0);
	shmctl(buf_id2, IPC_RMID, 0);
	shmctl(buf_id3, IPC_RMID, 0);
	shmctl(buf_id4, IPC_RMID, 0);
	shmctl(buf_id5, IPC_RMID, 0);
	shmctl(buf_id6, IPC_RMID, 0);
	shmctl(buf_id7, IPC_RMID, 0);
	return 0;
}