#include <iostream>
#include <vector>
#include <cstring>
#include <algorithm>
#include <cassert>
#include <deque>
#include <map>
#include <queue>
#include <climits>
#include <set>
#include <stack>
#include <cmath>
#include <unordered_map>
#include <unordered_set>

using namespace std;

#define ll long long
#define MAX 100000
#define MAX1 10000
#define MAX2 100000
#define inf 1000000000
#define eps 1e-12
#define MOD 1000000007

int main() {

    ios_base::sync_with_stdio(0);
    cin.tie(0);

    int a[MAX*10 + 5] = {};
    for (int i = 1; i <= MAX*10; ++i) {
        for (int j = i; j <= MAX*10; j += i) {
            a[j]++;
        }
    }
    cout << "HelloWorld\n";
//    cout << "Time Elapsed : " << (1.0 * clock()) / CLOCKS_PER_SEC;

    return 0;
}
