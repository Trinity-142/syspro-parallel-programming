import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('results.csv')
df['Param: counterType'] = df['Param: counterType'].fillna('Baseline')
def plot(bench, method):
    df_inc = df[(df['Benchmark'].str.contains(bench)) & (df['Benchmark'].str.contains(method))]
    plt.figure(figsize=(10, 6))
    plt.title(f'Scalability of {method} operation')
    plt.xlabel('Number of Threads')
    plt.ylabel('Throughput (ops/ms)')
    plt.grid(True)

    counter_types = df_inc['Param: counterType'].unique()
    for c_type in counter_types:
        subset = df_inc[df_inc['Param: counterType'] == c_type].sort_values(by='Threads')
        plt.errorbar(
            subset['Threads'],
            subset['Score'],
            yerr=subset['Score Error (99.9%)'],
            label=c_type,
            marker='o',
            capsize=5
        )

    plt.legend()
    plt.tight_layout()
    plt.yscale('log')
    plt.tight_layout()
    plt.savefig(f'{bench}_{method}_plot.png')

plot("CountersBenchmark", "inc")
plot("NoContentionBaseline", "inc")
plot("CountersBenchmark", "get")
plot("NoContentionBaseline", "get")
