# GGMusic 部署指南

## 1. 环境准备 (服务器端)

确保你的服务器 (121.41.74.173) 满足以下条件：
*   **Java 17**: 运行 `java -version` 检查。如果没有，请安装：
    ```bash
    yum install java-17-openjdk -y  # CentOS
    # 或
    apt install openjdk-17-jdk -y   # Ubuntu
    ```
*   **MySQL**: 确保数据库服务正常运行，且存在 `ggmusic` 数据库。
*   **文件目录**: 创建用于存储音乐文件的目录：
    ```bash
    mkdir -p /root/ggmusic_data/
    chmod 777 /root/ggmusic_data/
    ```

## 2. 打包项目 (本地)

在 IDEA 的 Terminal 中执行：
```bash
mvn clean package -DskipTests
```
打包完成后，在 `target` 目录下会生成一个 `GGMusic-0.0.1-SNAPSHOT.jar` 文件。

## 3. 上传文件

使用 SCP 或 SFTP 工具（如 Xshell, WinSCP）将 jar 包上传到服务器的 `/root/` 目录。

## 4. 运行项目

由于服务器内存有限（1G），我们需要限制 JVM 内存。

**前台运行 (测试用):**
```bash
java -Xmx256m -jar GGMusic-0.0.1-SNAPSHOT.jar
```
如果看到 "Started GGMusicApplication"，说明启动成功。按 `Ctrl+C` 停止。

**后台运行 (正式部署):**
```bash
nohup java -Xmx256m -jar GGMusic-0.0.1-SNAPSHOT.jar > ggmusic.log 2>&1 &
```

## 5. 验证

访问：`http://121.41.74.173:8081`

## 6. 停止服务

如果需要停止服务：
```bash
# 找到进程ID
ps -ef | grep GGMusic
# 杀掉进程
kill -9 <PID>
```
