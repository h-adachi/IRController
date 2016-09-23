# IRController

## 概要
+ 赤外線センサーを使用して任意のコマンドを送信するアプリ

## 開発環境
+ Windows 10
+ Android Studio 2.2
+ Galaxy Tab
+ Android Emu

## 注意
+ ConsumerIrManagerを使っているのでAndroid4.4系以上でしか動かないはず
+ 38kHzの赤外線発信可能なデバイスがないと動作しません
+ エラー制御を余り書いていないのでそれなりに

## 入力
+ 数値系は16進数表記

## TODO
### フォーマット追加
+ AEHAフォーマット
+ SONYフォーマット

## 備考
### 参考資料等
http://elm-chan.org/docs/ir_format.html  
http://www.ne.jp/asahi/o-family/extdisk/ATBextension/ATBIRrecvNEC/ATBIRrecvNEClst.pdf
