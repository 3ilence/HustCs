#include <cstdio>
#include <iostream>
#include <map>
#include <cstring>
using namespace std;
int main(void)
{
    map<string, string> dic;
    map<string, bool> flag;
    char buf1[100], buf2[100], buf3[100];
    string key, value;
    freopen("2503.txt", "r", stdin);
    while (1)
    {
        scanf("%s", buf1);
        char temp = getchar();
        if (temp == '\n')
            break;
        scanf("%s", buf2);
        value.assign(buf1, strlen(buf1));
        key.assign(buf2, strlen(buf2));
        //printf("value:%s key:%s\n", buf1, buf2);
        dic.insert(pair<string, string>(key, value));
        flag.insert(pair<string, bool>(key, true));
        //map[key] = value;
    }
    int i = 0;
    key.assign(buf1, strlen(buf1));
    if (!flag[key])
        cout << "eh" << endl;
    else
        cout << dic[key] << endl;
    while (cin >> key)
    {
        if (!flag[key])
            cout << "eh" << endl;
        else
            cout << dic[key] << endl;
    }
    fclose(stdin);
    getchar();
    return 0;
}
/*
dog ogday
cat atcay
pig igpay
froot ootfray
loops oopslay

atcay
ittenkay
oopslay
*/