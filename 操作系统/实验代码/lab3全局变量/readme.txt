实验三要求使用execv api，我这种写法是会把三个文件编译成一个可执行文件
是不符合要求。正确的应该是三个源程序分别编译成三个可执行程序，由lab3源文件execv api
去加载另外两个可执行文件。正确写法见另外两个文件夹。