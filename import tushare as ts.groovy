import tushare as ts
import matplotlib.pyplot as plt
from mpl_finance import candlestick_ochl
from matplotlib.pylab import date2num
import pandas as pd
import numpy as np

# 获取贵州茅台从2018年元旦至2023年3月20日的行情数据
df = ts.get_k_data('600519', start='2018-01-01', end='2023-03-20')

# 计算MACD指标
def MACD(df, fast=12, slow=26, signal=9):
    """
    计算MACD指标
    """
    df = df.copy()
    df['EMA_fast'] = df['close'].ewm(span=fast, min_periods=fast).mean()
    df['EMA_slow'] = df['close'].ewm(span=slow, min_periods=slow).mean()
    df['DIF'] = df['EMA_fast'] - df['EMA_slow']
    df['DEA'] = df['DIF'].ewm(span=signal, min_periods=signal).mean()
    df['MACD'] = 2 * (df['DIF'] - df['DEA'])
    return df

df_macd = MACD(df)

# 画图展示
df_macd['time'] = pd.to_datetime(df_macd['date'])
df_macd['time'] = df_macd['time'].apply(date2num)
df_values = [tuple(vals) for vals in df_macd[['time', 'open', 'close', 'high', 'low']].values]

fig, ax = plt.subplots(figsize=(20, 10))
candlestick_ochl(ax, df_values, width=0.6, colorup='red', colordown='green', alpha=0.8)
plt.plot(df_macd['time'], df_macd['DIF'], label='DIF', color='blue', alpha=0.8)
plt.plot(df_macd['time'], df_macd['DEA'], label='DEA', color='orange', alpha=0.8)
plt.bar(df_macd['time'], df_macd['MACD'], label='MACD', color='grey', alpha=0.5)
plt.legend(loc='best')
plt.title('Guizhou Maotai Stock Price and MACD')
plt.xlabel('Date')
plt.ylabel('Price')
plt.show()