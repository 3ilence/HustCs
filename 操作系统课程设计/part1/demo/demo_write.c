#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main()
{
    int real_num = write(1,"This is a demo to use method write.\n",36);
    if(real_num==36)
    {
        write(1,"write successfully\n",19);
        write(0,"success!\n",9);
        //如果写入到标准输入里，程序结束时会自动将标准输入里的内容输出
    }

    exit(0);
}
