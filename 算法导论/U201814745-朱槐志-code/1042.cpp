#include <algorithm>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string.h>
using namespace std;
#define maxN 30
int h;                                         //钓鱼时间[1,16]
int n;                                         //湖的个数[2,25]
int f_initial[maxN + 1], d[maxN + 1], t[maxN]; //因为下标0不准备要，所以最大容量需要加一

//t3 = 4 means that it takes 20 minutes to travel from lake 3 to lake 4
//输入格式
//n
//h
//fi,1<= i < n,fi是最开始的五分钟的时间内可以在编号为i的湖中能钓到的鱼的数量
//di,1<= i < n，在同一个湖里钓鱼的时间每增加五分钟，能钓到的鱼就会在之前基础上减去di，
//当能钓到的鱼数量小于等于di时，下一个五分钟将无鱼可钓
//用在每个湖的时间必须是5min的倍数
//ti,1<= i < n - 1
//枚举所有池塘作为终点时贪心结果
int main(void)
{
    //freopen("1042.txt", "r", stdin);
    while (scanf("%d", &n), n != 0)
    {
        scanf("%d", &h);
        for (int i = 1; i <= n; i++)
        {
            scanf("%d", f_initial + i);
            //cout << f[i] << " ";
        }
        //cout << endl;
        for (int i = 1; i <= n; i++)
        {
            scanf("%d", d + i);
            //cout << d[i] << " ";
        }
        //cout << endl;
        for (int i = 1; i <= n - 1; i++)
        {
            scanf("%d", t + i);
            //cout << t[i] << " ";
        }
        //cout << endl;
        int sum_t[n + 1]; //把编号为n的池塘作为终点时的用来行走的时间
        int sum_f[n + 1]; //把编号为n的池塘作为终点时用于钓鱼的时间
        memset(sum_t, 0, sizeof(sum_t));
        memset(sum_f, 0, sizeof(sum_f));

        int max_fish = 0;
        int choose_last = 0; //当终点池塘序号为choose_last时钓到鱼最多

        int last_nf[n + 1];
        memset(last_nf, 0, sizeof(last_nf));
        for (int i = 1; i <= n; i++)
        {
            int f[maxN + 1]; //一开始直接用的f_initial，
            memset(f, 0, sizeof(f));
            //然后因为在i =1时f_initial[1]已经由10减为0，且又没有重新被初始化，造成错误
            //所以需要另外复制一个f[maxN + 1],表示剩下的鱼
            for (int k = 1; k <= n; k++)
                f[k] = f_initial[k];
            int n_f[n + 1]; //把编号为n的池塘作为终点时在编号为n的池塘钓鱼的总时间
            memset(n_f, 0, sizeof(n_f));
            int fish = 0; //把编号为n的池塘作为终点时,钓到鱼的总数
            for (int j = 1; j < i; j++)
                sum_t[i] += t[j];
            //printf("when choose %d seqnum pool as last point，walking time is：%d\n", i, sum_t[i]);
            sum_f[i] = h * 12 - sum_t[i]; //用于钓鱼的总时间
            /*
            if(sum_f[i] <= 0)
            {
                break;
            }*/
            //包含在循环条件里了
            //no time for fishing
            //printf("when choose %d seqnum pool as last point,fishing time is:%d\n", i, sum_f[i]);
            int count = 1;       //debug
            while (sum_f[i] > 0) //当还有时间剩余，选择最优的池塘进行这次钓鱼
            {
                int choose = 0; //最终选择的池塘的编号[1~n]
                int sub_max_fish = 0;
                for (int j = 1; j <= i; j++)
                {
                    if (f[j] > sub_max_fish) //如果两个池塘的钓鱼期望相同，会使用编号较小的池塘
                    {
                        sub_max_fish = f[j];
                        choose = j;
                    }
                }
                if (sub_max_fish == 0) //当所有池塘都没有鱼可以钓的时候退出循环
                {
                    for (int k = 1; k <= n; k++)
                    {
                        if (f[k] == 0)
                        {
                            n_f[k] += sum_f[i];
                            break;
                        }
                    }
                    break;
                } //如果不是以钓鱼时间用完的方式结束的话，那么需要把剩余的时间全部加到第一个f[k] == 0的池塘里去
                //printf("%dst choose,choose seqnum %d pool,will get %d fish\n", count, choose, f[choose]);
                if (f[choose] > d[choose])
                    f[choose] = f[choose] - d[choose];
                else
                    f[choose] = 0;
                n_f[choose]++;

                fish += sub_max_fish;
                count++;
                sum_f[i]--;
            }
            if (max_fish < fish)
            {
                max_fish = fish;
                for (int k = 1; k <= n; k++)
                {
                    choose_last = i;
                    last_nf[k] = n_f[k];
                }
            }
        }
        for (int j = 1; j <= n; j++)
        {
            cout << 5 * last_nf[j] << ", ";
            if (j == n - 1)
            {
                cout << 5 * last_nf[++j] << endl;
                break;
            }
        }
        cout << "Number of fish expected: " << max_fish << endl
             << endl;
    }
    //fclose(stdin);
    return 0;
}
