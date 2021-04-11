#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main()
{
    char buf[128] = {0};
    int real_num;

    real_num = read(0,buf,128);
    if(real_num==-1)//∂¡»° ß∞‹
    {
        write(2,"a error happened\n",17);
    }
    else
    {
        write(1,"succuss",7);
    }

    exit(0);
}