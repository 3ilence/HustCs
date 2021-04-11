//Memory Limit Exceeded超出空间限制
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <string.h>
#include <iostream>
#include <algorithm>
#include <vector>
#include <cmath>
bool test(int val);
using namespace std;
int N = 0, M = 0; //3-10000
int series[100005];

int main(void)
{
    freopen("3579.txt", "r", stdin);
    while (scanf("%d", &N) != EOF)
    {
        M = N * (N - 1) / 4;
        for (int i = 0; i < N; i++)
        {
            scanf("%d", series + i);
        }
        sort(series, series + N); //从小到大排序
        int up_bound = series[N - 1] - series[0] + 1;
        int low_bound = 0; //下闭上开
        while (up_bound - low_bound > 1)
        {
            int mid = (up_bound + low_bound) >> 1; //带符号的右移
            if (test(mid))
                low_bound = mid;
            else
                up_bound = mid;
        }
        cout << low_bound << endl;
    }
    fclose(stdin);
    return 0;
}

bool test(int val)
{
    //判断a[j] - a[i] <= val - 1的是否大于M
    int cnt = 0;
    for (int i = 0; i < N; i++)
    {
        cnt += (N - 1) - (lower_bound(series + i + 1, series + N, series[i] + val) - series - 1);
    } //lower_bound(series + i + 1, series + N, series[i] + val) - series
    //lower_bound是找到第一个大于或等于的元素的地址
    //在series[i+1]~series[N]
    if (cnt > M)
        return true;
    else
        return false;
}