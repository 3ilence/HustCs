#include<signal.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<sys/wait.h>
#include<unistd.h>
#include<fcntl.h>
#include<stdio.h>
#include<stdlib.h>
#include<errno.h>
#include<assert.h>
#include<string.h>
#define DEN_BUF 1024
int filedes[2];
int pid1=0;
int pid2=0;
int count = 1;
char sendBuf[DEN_BUF] = "I send you 1 times";
char rcvBuf[DEN_BUF];
void signpass(int sign);
void clean(int sign);

int main()
{
    printf("Process on...\n");
    assert (pipe(filedes)>=0);
    signal(SIGINT,signpass);

    assert ( ( pid1 = fork() ) >=0 );
    if( pid1 == 0 )
    {
        signal(SIGINT,SIG_IGN);//Ignore Keyboard Interrupt
        signal(SIGUSR1,clean);//SIGUSR is reserved for the user;When receive this signal,run clean
        //为什么如果在读操作的时候不关闭写管道的话，程序不会正常退出
        close(filedes[1]);//stop writing
        //readpipe1(filedes[0]);//read from parent process
        while (1)
        {
            if(count == 10)
                exit(0);
            //printf("[子进程2]此时count为%d\n",count);
            memset(rcvBuf,0,DEN_BUF);
            if(read(filedes[0],rcvBuf,DEN_BUF) == 0)
            {
                //printf("Child Process 1 is killed by parent!\n");
                exit(0);
            }

		    printf("子进程1：%s\n",rcvBuf);
            //buf[n]='\0';//add tail
            //sendBuf[11] += 1;
            count++;
            sleep(1);

        }
        //exit(0)
    }
    else
    {
        assert((pid2=fork())>=0);
        if (pid2==0)//Sub process2
        {
            signal(SIGINT,SIG_IGN);
            signal(SIGUSR1,clean);//Clean process2
            close(filedes[1]);//stop writing
            //readpipe2(filedes[0]);//read from parent process

            while(1)
            {
                //printf("[子进程1]此时count为%d\n",count);
                memset(rcvBuf,0,DEN_BUF);
                if(read(filedes[0],rcvBuf,DEN_BUF) == 0)
                {
                    //printf("Child Process 2 is killed by parent!\n");
                    exit(0);
                }
		        printf("子进程2：%s\n",rcvBuf);
                //buf[n]='\0';//add tail
                //sendBuf[11] += 1;
                count++;

                sleep(1);
            }
            //exit(0);
        }
        else {
            while(1)
            {
                if(count == 10)
                {
                    close(filedes[0]);
                    close(filedes[1]);
                    break;
                }
                //printf("[主进程]此时count为%d\n",count);
                close(filedes[0]);//Stop reading
                sendBuf[11] = '0' + count;
                /*
                if(count > 9 )
                */
                write(filedes[1],sendBuf,DEN_BUF);//Write msg to pipe
                count++;
                sleep(1);
            }
            waitpid(pid1,NULL,0);
            printf("Child Process 1 is killed by parent!\n");
            waitpid(pid2,NULL,0);//Waiting until all sub processes finish
            printf("Child Process 2 is killed by parent!\n");
            printf("Parent Process is killed!\n");
        }
    }

    return 0;
}


void signpass(int sign)
//To pass end sign to process 1,2
{
    kill(pid1,SIGUSR1);
    kill(pid2,SIGUSR1);
}
void clean(int sign)
{
    close(filedes[0]);
    close(filedes[1]);//Close Pipe
    if(pid1==0)
    {
        printf("Child Process 1 is killed by parent!\n");
        exit(0);
    }

    if(pid2==0)
    {
        printf("Child Process 2 is killed by parent!\n");
        exit(0);
    }
}