#include <stdio.h>
#include <linux/kernel.h>
#include <unistd.h>
#include <sys/syscall.h>
//本程序是用来测试系统调用函数是否配置成功的
//dmesg命令可以显示系统调用函数printk内容
int main(void)
{
	syscall(336, "tanzhilang.JPG", "target.JPG");
	//syscall(336, "source.txt", "target.txt");
	int hello = syscall(335, 100);
	printf("%d", hello);
	return 0;
}
/*
asmlinkage int sys_helloworld(int number);
*/

/*
SYSCALL_DEFINE1(helloworld,int,number)
{
	printk("Hello world!");
	return number+1;
}
*/

/*
335	64	helloworld	sys_helloworld
*/