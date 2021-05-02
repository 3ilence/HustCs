#include <getopt.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <assert.h>
#include <math.h>
#include <limits.h>
#include <string.h>
#include <errno.h>
#include "cachelab.h"

//#define DEBUG_ON

//定义地址长度位64位
#define ADDRESS_LENGTH 64


//为内存地址的数据类型起别名
typedef unsigned long long int mem_addr_t;


//定义cache行的结构
typedef struct cache_line {
    char valid;
    mem_addr_t tag;
    //count表示该cache已经连续多少次没有被访问
    unsigned long long int count;
} cache_line_t;

//cache_set_t表示一个cache组
typedef cache_line_t* cache_set_t;
//多个cache组构成一个完整的cache
typedef cache_set_t* cache_t;

/*命令行参数的标志 */
//是否打印trace
int verbosity = 0;
int s = 0;  //pow(2,s)表示有多少组
int b = 0;  //内存块内地址位数offset位数
int E = 0;  //关联度 每一个组内有几个快，一块就是一个cache行
//内存访问轨迹文件名
char* trace_file = NULL;

//由命令行参数推导得出
int S; // S=pow(2,s)多少个组
int B; //pow(2,b)组相连每一个组的大小，也就是字节数

/* 统计结果数据 */
int miss_count = 0;
int hit_count = 0;
int eviction_count = 0;
unsigned long long int lru_counter = 1;//

//本次用到的模拟cache
cache_t cache;
//与运算掩码
mem_addr_t set_index_mask;


//cache初始化，分配空间
void initCache()
{
    int i,j;
    cache = (cache_set_t*) malloc(sizeof(cache_set_t) * S);//一个组×组数
    for (i=0; i<S; i++){
        cache[i]=(cache_line_t*) malloc(sizeof(cache_line_t) * E);
        for (j=0; j<E; j++){
            cache[i][j].valid = 0;
            cache[i][j].tag = 0;
            cache[i][j].count = 0;
        }
    }

    //一个address分成三部分：tag index offset
    //                    数据    s    b
    //用来做与运算得到一个内存地址对应的组编号,
    set_index_mask = (mem_addr_t) (pow(2, s) - 1);
}

//释放cache内存
void freeCache()
{
    int i;
	for(i=0;i<S;i++)
	{
        //释放cache组指针
		free(cache[i]);
	}
    //释放cache本身指针
	free(cache);
}
/*
访问缓存的数据，如果成功hit++，如果失败需要判断是否还有空闲缓存行
如果没有空闲缓存行则选择count最大的缓存行替换
*/
void loadData(mem_addr_t addr)
{
    int i;
    //如果最终miss的话需要找到要替换的cache行
	int evicted_line = 0;
    //被替换的cache行的count，count表示该cache行连续没有被访问的次数
	unsigned long long int evicted_lru = 0;

    //得到组编号index
    mem_addr_t set_index = (addr >> b) & set_index_mask;
    //放在一个cache行里面的tag
    mem_addr_t tag = addr >> (s+b);
    //是否命中
    int hit = 0;

    //提取出那一组
    cache_set_t cache_set = cache[set_index];
    //E:相联度 即每一个组内有几块
	for (i = 0; i < E; i++)
	{
        //在如果tag相等且cache行不为空，表示命中
		if(cache_set[i].tag == tag && cache_set[i].valid == 1)
		{
            printf("hit ");
			hit_count++;
            //命中之后就把count置为0，表示刚被访问过
			cache_set[i].count = 0;
            hit = 1;
		}
        else
        {
            if(cache_set[i].valid == 1)
            {
                //没命中的话就需要将count++，表示该cache行又没有被访问
                cache_set[i].count ++;
            }
        }

	}
    //不在cache中，未命中
	if(hit == 0)
	{
        printf("miss ");
        int j;
		miss_count++;
        //决定需要放入cache的地方
		for(j = 0;j < E; j++)
		{
            //先判断有没有空闲的cache行来让我们存入数据
			if(cache_set[j].valid == 0)//无效说明没有用过，空闲位置
			{
				evicted_line = j;
                break;
			}
			else
			{
                //得到最久没有被访问的cache行，即count最大的cache行
				if(cache_set[j].count > evicted_lru)
				{
					evicted_lru = cache_set[j].count;
					evicted_line = j;
				}
			}
		}
        //valid==0 表明没有缓存行的替换
		if(!(cache_set[evicted_line].valid == 0))//有淘汰
		{
            printf("eviction ");
			eviction_count ++;
		}
		cache_set[evicted_line].valid =1;
		cache_set[evicted_line].count =0;
		cache_set[evicted_line].tag =tag;
	}

}


/*
    读取trace文件然后模拟cache行为
*/
void replayTrace(char* trace_fn)
{
    mem_addr_t addr = 0;
    unsigned int len = 0;
    FILE* trace_fp = fopen(trace_fn, "r");

	char operation= fgetc(trace_fp);

	while(operation != EOF )
	{
		switch(operation)
		{

            //数据修改
			case 'M':{
						fscanf(trace_fp,"%X, %d",(unsigned int*) &addr, &len);
                        printf("%c %x %u ",operation, (int)addr,len);
						loadData(addr);
                        loadData(addr);
                        printf("\n");
                        break;
			}
            //数据装载
			case 'L':{
				        fscanf(trace_fp,"%X,%d",(unsigned int*)&addr,&len);
                        printf("%c %x %u ",operation,(int)addr,len);
                        loadData(addr);
                        printf("\n");
                        break;
			}
            //数据存储
			case 'S':{
						fscanf(trace_fp,"%X,%d",(unsigned int*)&addr,&len);
                        printf("%c %x %u ",operation,(int)addr,len);
                        loadData(addr);
                        printf("\n");
                        break;
			}
            //指令装载
			case 'I':{
						fscanf(trace_fp,"%X,%d",(unsigned int*)&addr,&len);//不处理
						printf("%c %x %u ",operation,(int)addr,len);
                        printf("\n");
                        break;
			}
			default:break;
		}
        operation = fgetc(trace_fp);
	}
}

/*
    使用说明
 */
void printUsage(char* argv[])
{
    printf("Usage: %s [-hv] -s <num> -E <num> -b <num> -t <file>\n", argv[0]);
    printf("Options:\n");
    printf("  -h         Print this help message.\n");
    printf("  -v         Optional verbose flag.\n");
    printf("  -s <num>   Number of set index bits.\n");
    printf("  -E <num>   Number of lines per set.\n");
    printf("  -b <num>   Number of block offset bits.\n");
    printf("  -t <file>  Trace file.\n");
    printf("\nExamples:\n");
    printf("  linux>  %s -s 4 -E 1 -b 4 -t traces/yi.trace\n", argv[0]);
    printf("  linux>  %s -v -s 8 -E 2 -b 4 -t traces/yi.trace\n", argv[0]);
    exit(0);
}

/*
    测试
 */
int main(int argc, char* argv[])
{
    char c;

    while( (c=getopt(argc,argv,"s:E:b:t:vh")) != -1){
        switch(c){
        case 's':
            s = atoi(optarg);
            break;
        case 'E':
            E = atoi(optarg);
            break;
        case 'b':
            b = atoi(optarg);
            break;
        case 't':
            trace_file = optarg;
            break;
        case 'v':
            verbosity = 1;
            break;
        case 'h':
            printUsage(argv);
            exit(0);
        default:
            printUsage(argv);
            exit(1);
        }
    }

    /* Make sure that all required command line args were specified */
    if (s == 0 || E == 0 || b == 0 || trace_file == NULL) {
        printf("%s: Missing required command line argument\n", argv[0]);
        printUsage(argv);
        exit(1);
    }

    /* 根据命令行参数计算S 和 B */
    //s 组索引位数
    //b 内存块内地址位数
    S = (unsigned int) pow(2, s);
    B = (unsigned int) pow(2, b);



    initCache();

#ifdef DEBUG_ON
    printf("DEBUG: S:%u E:%u B:%u trace:%s\n", S, E, B, trace_file);
    printf("DEBUG: set_index_mask: %llu\n", set_index_mask);
#endif

    replayTrace(trace_file);

    freeCache();

    printSummary(hit_count, miss_count, eviction_count);
    return 0;
}
