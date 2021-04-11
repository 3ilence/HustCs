#include <algorithm>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string.h>
#include <map>
#include <vector>
using namespace std;
#define maxN 20
int add_money(int &sum);
int N = 0;       //1~20
int wek_moy = 0; //1~10000000000
int value[maxN];
vector<int> new_value;
map<int, int> change;
int flag[maxN]; //用来标记new_value中的各个面额的钞票是否用完
int num[maxN];
int ans = 0; //number of weeks
int main(void)
{
    freopen("3040.txt", "r", stdin);
    memset(value, 0, sizeof(value));
    memset(num, 0, sizeof(num));
    memset(flag, 0, sizeof(flag));
    scanf("%d", &N);
    scanf("%d", &wek_moy);
    for (int i = 0; i < N; i++)
    {
        scanf("%d %d", value + i, num + i);
        //printf("%d %d\n",value[i],num[i]);
    }
    for (int i = 0; i < N; i++)
    {
        if (value[i] >= wek_moy)
        {
            ans += num[i];
            //cout << "ans is now:" << ans << endl;
        }
        else
        {
            new_value.push_back(value[i]);
            change[value[i]] = num[i];
            //cout << new_value[count] << " ";
        }
    }
    //cout << endl;
    sort(new_value.begin(), new_value.end());
    /*for (int i = 0; i < change.size(); i++)
    {
        //cout << new_value[i] << " " << change[new_value[i]] << endl;
    }*/

    while (new_value.size() > 0)
    {
        //每次循环最开始添加到当前最大面额的零钱到sub_sum
        //随后调用函数add_money,每次都仅加最小面额的零钱
        int sub_sum = 0;
        sub_sum += new_value[new_value.size() - 1];
        //cout << "the first added int is :" << new_value[new_value.size() - 1] << endl;
        change[new_value[new_value.size() - 1]]--;
        if (change[new_value[new_value.size() - 1]] == 0)
            new_value.pop_back(); //(new_value.end() - 1, new_value.end());
        if (!add_money(sub_sum))
            break;
        ans++;
        //cout << "ans is now:" << ans << endl;
    }
    cout << ans << endl;
    fclose(stdin);
    return 0;
}

int add_money(int &sum) //引用参数
{
    //cout << "sum is" << sum << endl;
    while (sum < wek_moy && new_value.size() != 0)
    {
        sum += new_value[0];
        //cout << "new_value is :" << new_value[0] << endl;
        change[new_value[0]]--;
        if (change[new_value[0]] == 0)
            new_value.erase(new_value.begin(), new_value.begin() + 1);
    }
    //cout << "the final sum is :" << sum << endl;
    if (sum < wek_moy) //说明零钱全都用光了
        return 0;
    return 1;
}