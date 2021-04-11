#include <stdio.h>
#include <stdlib.h>
#include "pthread.h"
#include "sys/types.h"
//#include "linux/sem.h"
#include <sys/ipc.h>
#include <sys/sem.h>

int semid1,semid2;
pthread_t p1,p2,p3;
int sum = 0;			//共享公共变量
int i = 1;
int end = 0;//判断是否是最后一个

union semun {
	int val; 					// value for SETVAL
	struct semid_ds *buf; 		// buffer for IPC_STAT, IPC_SET
	unsigned short *array; 		// array for GETALL, SETALL
	struct seminfo *__buf; 		// buffer for IPC_INFO
};
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
//每次加一个数，结果为偶数由打印线程1打印，奇数由打印线程2打印；
void* subp1()
{
// sum = 100 i = 1 a = 0
	while(1) {		//循环累加计算
		//printf("[Debug]subp1\n");
		P(semid1,0);
		if(i <= 100)
		{
			sum += i;
			printf("第%d次累加,累加结果为%d\n",i,sum);
			if(sum % 2 == 0)
				V(semid2,0);//偶数由线程2打印
			else
				V(semid2,1);//奇数由线程3打印
			i++;
		}
		else
		{
			end = 1;
			printf("累加完成时end为%d\n",end);
			printf("已经累加完成！\n");
			V(semid2,0);
			V(semid2,1);
			break;
		}

	}
	return NULL;
}
//偶数打印
void* subp2()
{
	while(1)
	{
		P(semid2,0);
		if (end != 1)
		{
			printf("[subp2]a is %d(是否是偶数)\n",sum);
			printf("[thread2(偶线程)]:the sum of 1~%d is %d\n",i - 1,sum);
			V(semid1,0);
		}
		else {
			printf("[thread2]已经累加完成！\n");
			break;
		}
	}
	/*while(i <= SUM)
	{
		if(a % 2 == 0 && a != 0)
		{

			P(semid,0);
			printf("[subp2]a 是 %d\n",a);
			if(a % 2 == 0)
				printf("a是偶数\n");
			else
			{
				printf("a是奇数\n");
			}

			printf("[thread2]:the sum of 1~%d is %d\n",i - 1,a);
			V(semid,1);
		}
	}*/
	return NULL;
}
//奇数打印
void* subp3()
{
	while(1)
	{
		P(semid2,1);
		if(end != 1)
		{
			printf("[subp3]sum is %d(是否是奇数)\n",sum);
			printf("[thread3(奇线程)]:the sum of 1~%d is %d\n",i - 1,sum);
			V(semid1,0);
		}
		else {
			printf("end等于%d\n",end);
			break;
		}
	}
	/*while(i <= SUM)
	{
		if(a % 2 == 1)
		{
			P(semid,0);
			printf("[subp3]i is %d\n",i);
			printf("[thread3]:the sum of 1~%d is %d\n",i - 1,a);
			V(semid,1);
		}
	}*/

	return NULL;
}




int main(void)
{
	union semun sem;
	//创建一个权限为666的信号量
	semid1 = semget(123,1,IPC_CREAT|0666);
	semid2 = semget(456,2,IPC_CREAT|0666);
	if(semid1 == -1 || semid2 == -1) {
        perror("create error\n");
        return 0;
    }
	//0号信号量初始化为1
	sem.val = 1;
	if(semctl(semid1,0,SETVAL,sem) != -1);
	//0和1号信号量初始化为0
	sem.val = 0;
	if(semctl(semid2,0,SETVAL,sem) != -1);
	if(semctl(semid2,1,SETVAL,sem) != -1);

	pthread_create(&p1,NULL,subp1,NULL);
	pthread_create(&p2,NULL,subp2,NULL);
	pthread_create(&p3,NULL,subp3,NULL);
	pthread_join(p1,NULL);
	pthread_join(p2,NULL);
	pthread_join(p3,NULL);
	//将信号灯集从内存中删除
	semctl(semid1,0,IPC_RMID,0);
	semctl(semid2,0,IPC_RMID,0);
    return 0;
}



//*********semget(MYKEY,2,IPC_CREAT|0666);
	//功能：创建一个新的信号量或获取一个已经存在的信号量的键值
	//返回值：成功返回信号量的标识码ID，失败返回-1；
	//参数一：一种情况是，键值是IPC_PRIVATE，该值通常为0，
	//意思就是创建一个仅能被进程给我的信号量，也可以自己指定一个键值
	//参数二：初始化信号量的个数
	//参数三：信号量的创建方式或权限。有IPC_CREAT，IPC_EXCL
	//IPC_CREAT如果信号量不存在，则创建一个信号量，否则获取。
	//IPC_EXCL只有信号量不存在的时候，新的信号量才建立，否则就产生错误。
//*********semctl(semid,1,SETVAL);
	//删除或者初始化信号量
	//成功返回正数，失败返回-1
	//参数一：信号标识码，即semget函数的返回值
	//参数二：操作信号在信号集中的编号，从0开始
	//参数三：命令，表示要进行的操作
	//命令SETVAL设置信号量集中一个单独的信号量的值，IPC_RMID将信号灯集semid从内存中删除
	//参数四:可选参数
//***********semop();
	//功能：用户改变信号量的值。也就是使用资源还是释放资源使用权。
	//返回值：成功返回0，失败返回-1；
	//参数一：信号标识码
	//参数二：一个指向结构体数组sedbuf的指针。
	//参数三：信号灯数组
	//参数四：信号灯个数
//***********pthread_create(&p1,NULL,subp1,NULL);
	//参数一：为指向线程标识符的指针。
	//参数二：用来设置线程属性。
	//参数三：是线程运行函数的起始地址。
	//参数四：是运行函数的参数。