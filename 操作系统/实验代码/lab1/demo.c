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


int main(){
	pid_t pid;
	int pipe_fd[2];
	int ret = pipe(pipe_fd);
	char buf[DEN_BUF];
	if(ret < 0){
		perror("pipe error\n");
		return -1;
	}
	pid = fork();
	if(pid < 0){
		perror("fork error\n");
	}
	else if(pid == 0){
	//	sleep(1);//sleep for 1 second
		close(pipe_fd[1]);
		memset(buf,0,DEN_BUF);
		read(pipe_fd[0],buf,DEN_BUF);
		printf("children has recived msg from father:\n");
		printf("%s\n", buf);
	}
	else{
		printf("father is sending  msg: \n");
		printf("请输入父进程要发送的数据： \n");
		close(pipe_fd[0]);
		memset(buf,0,DEN_BUF);
		fgets(buf,DEN_BUF,stdin);
		write(pipe_fd[1],buf,strlen(buf));
		//printf("father is sending msg\n");

	}

}