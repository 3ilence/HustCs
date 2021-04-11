#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <string.h>
#include <iostream>
#include <algorithm>
using namespace std;
//记忆以特定点开始的最长下降序列长度
//bool visited[10001][10001]; //记忆访问点
//int len[4][10001][10001];
int Len[101][101];    //最长递减序列的长度
int matrix[101][101]; //矩阵内容
int maxLen = -1111111;
int R = 0, C = 0;
int dfs(int i, int j);
int main(void)
{
    freopen("1088.txt", "r", stdin);
    memset(Len, 0, sizeof(Len));
    scanf("%d", &R);
    scanf("%d", &C);
    for (int i = 0; i < R; i++)
        for (int j = 0; j < C; j++)
            scanf("%d", &matrix[i][j]);
    for (int i = 0; i < R; i++)
    {
        for (int j = 0; j < C; j++)
        {
            maxLen = max(maxLen, dfs(i, j));
        }
    }
    cout << maxLen << endl;
    fclose(stdin);
    return 0;
}

int dfs(int i, int j)
{
    int len = 1;
    if (Len[i][j] != 0)
        return Len[i][j];
    int next[4][2] = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}}; //上下左右
    for (int k = 0; k < 4; k++)
    {
        int nextx = i + next[k][0];
        int nexty = j + next[k][1];
        if (nextx < 0 || nexty < 0 || nextx == R || nexty == C)
            continue;
        else if (matrix[nextx][nexty] > matrix[i][j])
            len = max(len, dfs(nextx, nexty) + 1);
    }
    Len[i][j] = len;
    return Len[i][j];
}
