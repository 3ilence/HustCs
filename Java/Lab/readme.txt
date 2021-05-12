Experiment1Test是测试程序，运行测试脚本即可得到测试结果，具体测试结果可以到output目录下的index.html里查看，
详见目录里的说明文件


实验项目文件夹SearchEngineForStudent下目录说明：

index目录下有四个文件，分别为index.dat，index.txt，searchResult.txt，phraseSearchResult.txt。
	index.dat是Index实例对象序列化得到的文件，
	index.txt是Index实例对象写入到文本文件的结果，
	searchResult.txt是单个单词搜索的结果，
	phraseSearchResult.txt是单词联合搜索或者短语搜索的搜索结果。
javadoc目录下是该项目生成的javadoc文档，入口文件为目录下的index.html。
out目录是编译后.class文件所在目录。
src是源码目录。
text是测试文本文件目录。
uml是各个类的uml图所在文件夹。

另外运行老师给的检测脚本时，106个测试案例中有一个failer，其实检查的时候是都pass了的，但是后来写报告的时候对功能做了修改
推测是因为我对Hit类里score计算方式做了修改，也可能是排序算法的问题，导致有一个案例测试出错。试着改了一会，太折磨了，
是在不太想改了，就这样了。
