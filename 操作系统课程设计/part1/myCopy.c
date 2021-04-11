#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc,char* argv[]){
	int fd,fd1,fd2;
	char bufs[1024];
	int len;
	if(argc != 3)
	{
		printf("参数错误\n");
	}
	else
	{
		//argv[1]是源文件
		//argv[2]为目标文件
		//打开源文件，返回新的文件描述符
		fd = open(argv[1],O_RDONLY|O_CREAT);
		if(fd != -1)
		{
			//先创建，创建失败再打开
			fd1 = creat(argv[2],0775);

			if(fd1 != -1)
			{
				fd2 = open(argv[2],O_WRONLY);
				while( (len = read(fd,bufs,1024)) > 0)
					write(fd2,bufs,len);
			}
			else
				printf("创建文件失败\n");
		}
		else
			printf("要复制的文件不存在\n");
	}
	return 0;
}