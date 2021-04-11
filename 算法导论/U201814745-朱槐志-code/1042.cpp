#include <algorithm>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string.h>
using namespace std;
#define maxN 30
int h;                                         //����ʱ��[1,16]
int n;                                         //���ĸ���[2,25]
int f_initial[maxN + 1], d[maxN + 1], t[maxN]; //��Ϊ�±�0��׼��Ҫ���������������Ҫ��һ

//t3 = 4 means that it takes 20 minutes to travel from lake 3 to lake 4
//�����ʽ
//n
//h
//fi,1<= i < n,fi���ʼ������ӵ�ʱ���ڿ����ڱ��Ϊi�ĺ����ܵ������������
//di,1<= i < n����ͬһ����������ʱ��ÿ��������ӣ��ܵ�������ͻ���֮ǰ�����ϼ�ȥdi��
//���ܵ�����������С�ڵ���diʱ����һ������ӽ�����ɵ�
//����ÿ������ʱ�������5min�ı���
//ti,1<= i < n - 1
//ö�����г�����Ϊ�յ�ʱ̰�Ľ��
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
        int sum_t[n + 1]; //�ѱ��Ϊn�ĳ�����Ϊ�յ�ʱ���������ߵ�ʱ��
        int sum_f[n + 1]; //�ѱ��Ϊn�ĳ�����Ϊ�յ�ʱ���ڵ����ʱ��
        memset(sum_t, 0, sizeof(sum_t));
        memset(sum_f, 0, sizeof(sum_f));

        int max_fish = 0;
        int choose_last = 0; //���յ�������Ϊchoose_lastʱ���������

        int last_nf[n + 1];
        memset(last_nf, 0, sizeof(last_nf));
        for (int i = 1; i <= n; i++)
        {
            int f[maxN + 1]; //һ��ʼֱ���õ�f_initial��
            memset(f, 0, sizeof(f));
            //Ȼ����Ϊ��i =1ʱf_initial[1]�Ѿ���10��Ϊ0������û�����±���ʼ������ɴ���
            //������Ҫ���⸴��һ��f[maxN + 1],��ʾʣ�µ���
            for (int k = 1; k <= n; k++)
                f[k] = f_initial[k];
            int n_f[n + 1]; //�ѱ��Ϊn�ĳ�����Ϊ�յ�ʱ�ڱ��Ϊn�ĳ����������ʱ��
            memset(n_f, 0, sizeof(n_f));
            int fish = 0; //�ѱ��Ϊn�ĳ�����Ϊ�յ�ʱ,�����������
            for (int j = 1; j < i; j++)
                sum_t[i] += t[j];
            //printf("when choose %d seqnum pool as last point��walking time is��%d\n", i, sum_t[i]);
            sum_f[i] = h * 12 - sum_t[i]; //���ڵ������ʱ��
            /*
            if(sum_f[i] <= 0)
            {
                break;
            }*/
            //������ѭ����������
            //no time for fishing
            //printf("when choose %d seqnum pool as last point,fishing time is:%d\n", i, sum_f[i]);
            int count = 1;       //debug
            while (sum_f[i] > 0) //������ʱ��ʣ�࣬ѡ�����ŵĳ���������ε���
            {
                int choose = 0; //����ѡ��ĳ����ı��[1~n]
                int sub_max_fish = 0;
                for (int j = 1; j <= i; j++)
                {
                    if (f[j] > sub_max_fish) //������������ĵ���������ͬ����ʹ�ñ�Ž�С�ĳ���
                    {
                        sub_max_fish = f[j];
                        choose = j;
                    }
                }
                if (sub_max_fish == 0) //�����г�����û������Ե���ʱ���˳�ѭ��
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
                } //��������Ե���ʱ������ķ�ʽ�����Ļ�����ô��Ҫ��ʣ���ʱ��ȫ���ӵ���һ��f[k] == 0�ĳ�����ȥ
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
