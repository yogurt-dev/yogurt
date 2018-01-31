github使用
-------
## git仓库管理
- fork  你想要贡献的代码库到私有库，例：flowable
- clone你的本地库将fork的代码下载到本地workspace
查看远程连接:
```
$git remote -v
```
- 此时你的连接应该是orgin为本地地址如:https://github.com/jianlin/flowable-engine.git
因为要对flowable做贡献，所以需要将workspace连接到flowable官方的远程git地址
```
$git remote add upflow https://github.com/flowable/flowable-engine.git
```
- 抓取flowable上最新的代码镜像
```shell
$git fetch upflow <分支名称> 
```
如:
```shell
$git fetch upflow 6.0.1
```
- 在本地check出一份最新分支
```shell
$git checkout -b isANewBranch upflow/6.0.1
```
现在就拥有了一份最新的upflow 6.0.1分支的代码了

## git的分支切换，如丝般顺滑git的分支切换，如丝般顺滑
- 用git就一定要用分支，开始会痛苦，用好了以后如鱼得水
查看分支命令
```shell
$git branch
```
切换分支命令
```shell
$git checkout <分支名称>
```

此处注意：用惯svn的人会始终觉得心惶惶，实则不必，你修改过的代码如果提交了，会跟着分支走，没提交会跟着你当前的切换一起带入新的分支

## git提交

```shell
$git diff
$git add .
$git status
$git commit -m "is a test commit"
$git log
```
提交代码至本地仓库
```shell
$git push origin isANewBranch
```
然后从git页面提一个pull request合并到主仓库
