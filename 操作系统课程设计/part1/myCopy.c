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
		printf("��������\n");
	}
	else
	{
		//argv[1]��Դ�ļ�
		//argv[2]ΪĿ���ļ�
		//��Դ�ļ��������µ��ļ�������
		fd = open(argv[1],O_RDONLY|O_CREAT);
		if(fd != -1)
		{
			//�ȴ���������ʧ���ٴ�
			fd1 = creat(argv[2],0775);

			if(fd1 != -1)
			{
				fd2 = open(argv[2],O_WRONLY);
				while( (len = read(fd,bufs,1024)) > 0)
					write(fd2,bufs,len);
			}
			else
				printf("�����ļ�ʧ��\n");
		}
		else
			printf("Ҫ���Ƶ��ļ�������\n");
	}
	return 0;
}