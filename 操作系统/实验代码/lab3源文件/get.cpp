#include <cstddef>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <iostream>
using namespace std;
#define NMAX 8
union semun
{
	int val;
	struct semid_ds *buf;
	unsigned short *array;
	struct seminfo *__buf;
};

unsigned char *buf[8];
const size_t buf_size = 2048;
FILE *in;
FILE *out;
int buf_id0 = 1, buf_id1 = 2, buf_id2 = 3, buf_id3 = 4, buf_id4 = 5, buf_id5 = 6, buf_id6 = 7, buf_id7 = 8;
int file_read = 100, file_write = 200, over = 300;
int buf_index; //缓冲区下标

void P(int semid, int semnum);

void V(int semid, int semnum);

int main(void)
{
	buf_id0 = shmget(1, buf_size, IPC_CREAT | 0666);
	buf_id1 = shmget(2, buf_size, IPC_CREAT | 0666);
	buf_id2 = shmget(3, buf_size, IPC_CREAT | 0666);
	buf_id3 = shmget(4, buf_size, IPC_CREAT | 0666);
	buf_id4 = shmget(5, buf_size, IPC_CREAT | 0666);
	buf_id5 = shmget(6, buf_size, IPC_CREAT | 0666);
	buf_id6 = shmget(7, buf_size, IPC_CREAT | 0666);
	buf_id7 = shmget(8, buf_size, IPC_CREAT | 0666);
	//共享缓冲区附接
	buf[0] = (unsigned char *)shmat(buf_id0, 0, 0);
	buf[1] = (unsigned char *)shmat(buf_id1, 0, 0);
	buf[2] = (unsigned char *)shmat(buf_id2, 0, 0);
	buf[3] = (unsigned char *)shmat(buf_id3, 0, 0);
	buf[4] = (unsigned char *)shmat(buf_id4, 0, 0);
	buf[5] = (unsigned char *)shmat(buf_id5, 0, 0);
	buf[6] = (unsigned char *)shmat(buf_id6, 0, 0);
	buf[7] = (unsigned char *)shmat(buf_id7, 0, 0);
	//获取信号量
	file_read = semget(100, 1, IPC_CREAT | 0666);
	file_write = semget(200, 1, IPC_CREAT | 0666);
	over = semget(300, 1, IPC_CREAT | 0666);
	//文件处理
	//char *s1,*s2;
	//scanf("%s %s",s1,s2);
	if ((in = fopen("in.txt", "rb")) == NULL)
	{
		//文件打开失败不需要关闭文件
		printf("can't open input file!\n");
		return 1;
	}
	if ((out = fopen("out.txt", "wb")) == NULL)
	{
		printf("can't open output file!\n");
		fclose(in);
		return 1;
	}
	//unsigned char *s = (unsigned char *)shmat(buf_id, 0, 0);
	//char ch;
	int count = 0, i = 0;
	//i是正在读取的缓冲区的标号
	puts("get:start");
	while (!feof(in))
	//while (1)
	{
		//cout<<"p操作之前读信号灯的值为："<<file_read<<endl;
		P(file_read, 0);
		//cout << "读信号灯的值为：" << file_read << endl;

		//加入内容
		//shmdt(s);
		//s = (unsigned char *)shmat(buf_id, 0, 0);
		buf_index = i % NMAX;
		count = fread(buf[buf_index], 1, buf_size, in); //每次从文件流中读取1024个unsigned char

		//ch = fgetc(in);
		//*s = ch;
		//printf("get:%c\n", *s);

		V(file_write, 0);
		printf("[get]%d\n", count);
		if (count < buf_size) //文件结束返回非0值，否则返回0
		{
			//printf("[get]第%d次读入字节不足1024字节\n", i);
			printf("[get]\n");
			printf("[get]最后的缓冲区号为%d\n", buf_index);
			//end = 1;
			//end_index =到达文件尾 buf_index; //结束时的缓冲区的下标
			//printf("[get]end_index为%d", end_index);
			break;
		}
		else
		{
			printf("[get]缓冲区%d读入成功!\n", buf_index);
			i++;
		}
	}
	fclose(in);
	//同步结束进程
	//put进程执行完之后，get进程紧跟着结束
	P(over, 0);

	puts("get:ended");
	//return 0;
	exit(0);
}

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