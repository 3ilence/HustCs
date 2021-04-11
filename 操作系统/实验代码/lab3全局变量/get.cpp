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

extern unsigned char *buf[8];
const size_t size = 2048;
extern FILE *in;
extern FILE *out;
extern int buf_id0, buf_id1, buf_id2, buf_id3, buf_id4, buf_id5, buf_id6, buf_id7;
//extern int buf_empty, buf_max, buf_mutex, part_over, over;
extern int file_read, file_write, over;
extern int buf_index; //缓冲区下标
//extern int end, end_index;

extern int P(int semid, int semnum);

extern int V(int semid, int semnum);

void get(void)
{
	//unsigned char *s = (unsigned char *)shmat(buf_id, 0, 0);
	//char ch;
	int count = 0, i = 0;
	//i是正在读取的缓冲区的标号
	puts("get:start");
	while (!feof(in))
	//while (1)
	{
		cout<<"p操作之前读信号灯的值为："<<file_read<<endl;
		P(file_read, 0);
		cout << "读信号灯的值为：" << file_read << endl;

		//加入内容
		//shmdt(s);
		//s = (unsigned char *)shmat(buf_id, 0, 0);
		buf_index = i % NMAX;
		count = fread(buf[buf_index], 1, size, in); //每次从文件流中读取1024个unsigned char

		//ch = fgetc(in);
		//*s = ch;
		//printf("get:%c\n", *s);

		V(file_write, 0);
		printf("[get]%d\n", count);
		if (count < size) //文件结束返回非0值，否则返回0
		{
			//printf("[get]第%d次读入字节不足1024字节\n", i);
			printf("[get]到达文件尾\n");
			printf("[get]最后的缓冲区号为%d\n", buf_index);
			//end = 1;
			//end_index = buf_index; //结束时的缓冲区的下标
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