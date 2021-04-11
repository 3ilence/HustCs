#include <cstddef>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <string.h>
#include <iostream>
#define NMAX 8 //缓冲区个数
using namespace std;
union semun
{
	int val;
	struct semid_ds *buf;
	unsigned short *array;
	struct seminfo *__buf;
};

unsigned char *buf[8];		  //字符指针数组
const size_t buf_size = 2048; //缓冲区大小
FILE *in;
FILE *out;
//共享缓冲区KEY
int buf_id0 = 1, buf_id1 = 2, buf_id2 = 3, buf_id3 = 4;
int buf_id4 = 5, buf_id5 = 6, buf_id6 = 7, buf_id7 = 8;
//信号量
int file_read = 100, file_write = 200, over = 300;
int buf_index; //缓冲区下标号
int len = 0;   //输入文件长度

void P(int semid, int semnum);
void V(int semid, int semnum);

int main(void)
{
	//共享缓冲区获取
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
	if ((in = fopen("./source/tanzhilang.JPG", "rb")) == NULL)
	{
		//文件打开失败不需要关闭文件
		printf("can't open input file!\n");
		return 1;
	}
	if ((out = fopen("./target/out.JPG", "wb")) == NULL)
	{
		printf("can't open output file!\n");
		fclose(in);
		return 1;
	}
	//计算输入文件长度
	fseek(in, 0, SEEK_END);
	len = ftell(in);
	fseek(in, 0, SEEK_SET);
	int count = 0, i = 0;
	puts("put:start");
	while (1)
	{
		P(file_write, 0);
		buf_index = i % NMAX;
		count = strlen((char *)buf[buf_index]);
		//如果剩余要读取的字节数小于buf_size，说明是最后一次写操作
		if (len < buf_size)
			fwrite(buf[buf_index], 1, len, out);
		else
			fwrite(buf[buf_index], 1, buf_size, out);

		V(file_read, 0);
		if (len < buf_size)
		{
			//printf("[put]第%d次写入不足1024字节\n", i);
			printf("[put]最后一次写操作,从缓冲区%d号读取了了%d个字符\n", buf_index, len);
			printf("[put]写入结束\n");
			break;
		}
		else
		{
			printf("[put]从缓冲区%d号读取了了%d个字符\n", buf_index, count);
			i++;
			len = len - buf_size; //每循环一次就少了size
		}
	}
	fclose(out);

	//同步结束进程
	V(over, 0);
	puts("put:ended");
	return 0;
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