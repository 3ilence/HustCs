#include <cstddef>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <string.h>
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
extern int buf_id0, buf_id1, buf_id2, buf_id3, buf_id4, buf_id5;
extern int buf_id6, buf_id7;
//extern int buf_empty, buf_max, buf_mutex, part_over, over;
extern int file_read, file_write, over;
extern int buf_index; //缓冲区下标号
extern int len;
//extern int end, end_index;

extern int P(int semid, int semnum);
extern int V(int semid, int semnum);

void put(void)
{
	//unsigned char *s = (unsigned char *)shmat(buf_id, 0, 0);
	int count = 0, i = 0; //
	//char ch;
	puts("put:start");
	while (1)
	{
		P(file_write, 0);
		//加入内容

		//shmdt(s);
		//s = (unsigned char *)shmat(buf_id, 0, 0);
		buf_index = i % NMAX;
		count = strlen((char *)buf[buf_index]);
		if (len < size)
			//fwrite(buf[buf_index], 1, size, out);
			fwrite(buf[buf_index], 1, len, out); //向文件流中写入个unsigned char
		else
			fwrite(buf[buf_index], 1, size, out);

		//printf("put:%c\n", ch);
		V(file_read, 0);
		/*if(ch != EOF){
            fputc(ch, out);
        }*/
		if (len < size)
		{
			//printf("[put]第%d次写入不足1024字节\n", i);
			printf("[put]从缓冲区%d号读取了了%d个字符\n", buf_index, count);
			printf("[put]写入结束\n");
			break;
		}
		else
		{
			printf("[put]从缓冲区%d号读取了了%d个字符\n", buf_index, count);
			i++;
			len = len - size; //每循环一次就少了size
		}

		//printf("[put]end为%d,end_index为%d\n", end, end_index);
		/*if (end == 1)
		{
			if (buf_index = end_index)
			{
				printf("[put]结束\n");
				break;
			}
		}*/
	}
	fclose(out);

	//同步结束进程
	V(over, 0);

	puts("put:ended");
	exit(0);
}