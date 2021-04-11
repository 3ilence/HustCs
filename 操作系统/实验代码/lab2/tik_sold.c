#include <stdio.h>
#include <stdlib.h>
#include "pthread.h"
#include "sys/types.h"
//#include "linux/sem.h"
#include <sys/ipc.h>
#include <sys/sem.h>
#define MYKEY 6666
#define SUM 100//总共100张票
union semun {
	int val; 					// value for SETVAL
	struct semid_ds *buf; 		// buffer for IPC_STAT, IPC_SET
	unsigned short *array; 		// array for GETALL, SETALL
	struct seminfo *__buf; 		// buffer for IPC_INFO
};

int outsell = 0;//销售量
int semid;
int times = 0;//销售次数
pthread_t tik1,tik2;
int end = 0;//结束判断的标记

void P(int semid,int index){
	struct sembuf sem;
	sem.sem_num = index;
	sem.sem_op = -1;
	sem.sem_flg = 0;
	semop(semid,&sem,1);
	return;
}

void V(int semid,int index){
	struct sembuf sem;
	sem.sem_num = index;//将要处理的信号灯的下标
	sem.sem_op =  1;//要执行的操作
	sem.sem_flg = 0;////操作标记：0或IPC_NOWAIT等
	semop(semid,&sem,1);//1:表示执行命令的个数
	return;
}
void *tik1_s()
{
    int sold1 = 0;//线程1售票总数
    while(1)
    {
        P(semid,0);
		if(end == 1)
			break;
		if(outsell < SUM)
		{
			outsell++;
       	 	sold1++;
        	times++;
        	printf("[thread1]sold plus 1.\n");
			printf("这是[thread1]卖出的第%d张票\n",sold1);
        	V(semid,0);
		}
		else {
			printf("票已经售完了！\n");
			end = 1;
			V(semid,0);
			break;
		}

    }
    printf("[thread1]has sold %d tikcets.\n",sold1);
    return NULL;
}

void *tik2_s()
{
    int sold2 = 0;//线程2售票总数
    while(1)
    {

        P(semid,0);
		//if(end == 1)
		//	break;
		if(outsell < SUM)
		{
			outsell++;
        	sold2++;
        	times++;
        	printf("[thread2]sold plus 1.\n");
			printf("这是[thread2]卖出的第%d张票\n",sold2);
        	V(semid,0);
		}
		else {
			printf("票已经售完了！\n");
			end = 1;
			V(semid,0);
			break;
		}
    }
    printf("[thread2]has sold %d tikcets.\n",sold2);
    printf("总销售次数为：%d\n",times);
    return NULL;
}

int main(void)
{
	union semun sem;
	//创建一个权限为666的信号量
	semid = semget(MYKEY,1,IPC_CREAT|0666);
	if(semid == -1) {
        perror("create error\n");
        return 0;
    }
	//0号信号量初始化为0
	sem.val = 1;
	if(semctl(semid,0,SETVAL,sem) != -1);
	//sem.val = 1;
	//if(semctl(semid,1,SETVAL,sem) != -1);
	//2号信号量初始化为1
	//sem.val = 1;
	//if(semctl(semid,2,SETVAL,sem) != -1);


	pthread_create(&tik1,NULL,tik1_s,NULL);
	pthread_create(&tik2,NULL,tik2_s,NULL);
	pthread_join(tik1,NULL);
	pthread_join(tik2,NULL);
	//将信号灯集从内存中删除
	semctl(semid,0,IPC_RMID,0);
    return 0;
}