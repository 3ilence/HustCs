#include <cstdio>
#include <string.h>
#include <string>
#include <iostream>
#include <algorithm>
using namespace std;

#define maxn 105

int array[maxn][maxn];
int sub[maxn][maxn][maxn];

int main()
{
    freopen("1050.txt", "r", stdin);
    int n;
    cin >> n;
    for (int i = 1; i <= n; i++)
    {
        for (int j = 1; j <= n; j++)
        {
            scanf("%d", &array[i][j]);
        }
    }
    memset(sub, 0, sizeof(sub));
    int ans = -1111111;
    for (int i = 1; i <= n; i++)
    {
        for (int j = 1; j <= n; j++)
        {
            int sum = 0;
            for (int k = j; k <= n; k++)
            {
                sum += array[i][k];
                sub[i][j][k] = max(sub[i - 1][j][k] + sum, sum); //i是指行，j是起始列，k是终结列，f存的值为在ijk范围内的元素和最大值
                ans = max(ans, sub[i][j][k]);
            }
        }
    }
    cout << ans << endl;
    fclose(stdin);
    return 0;
}