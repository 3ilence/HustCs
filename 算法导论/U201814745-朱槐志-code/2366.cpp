#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;
bool binary_search(vector<int> &a, vector<int> &b);
int main(void)
{
    vector<int> a, b;
    int input = 0;
    int n = 0;
    freopen("2366.txt", "r", stdin);
    scanf("%d", &n);
    for (int i = 0; i < n; i++)
    {
        scanf("%d", &input);
        a.push_back(input);
    }
    scanf("%d", &n);
    for (int i = 0; i < n; i++)
    {
        scanf("%d", &input);
        b.push_back(input);
    }
    if (binary_search(a, b))
        cout << "YES" << endl;
    else
        cout << "NO" << endl;
    fclose(stdin);
    //system("pause");
    return 0;
}
bool binary_search(vector<int> &a, vector<int> &b)
{
    int size1 = a.size();
    int size2 = b.size();
    int res = 0;
    for (int i = 0; i < size1; i++)
    {
        res = 10000 - a[i];
        int high = 0, low = size2 - 1;
        int mid = 0;
        while (high <= low)
        {
            mid = high + (low - high) / 2;
            if (b[mid] == res)
                return true;
            else if (b[mid] > res)
                high = mid + 1;
            else
                low = mid - 1;
        }
    }
    return false;
}
