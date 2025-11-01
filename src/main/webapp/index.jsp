<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>首页</title>
    <link href="css/index.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<h3> 你好，<span id="welcomeUsername" class="bg-white/20 px-2 py-1 rounded-md relative inline-block">
                            用户
                            <span class="absolute -bottom-1 left-0 w-full h-1 bg-white/40 rounded-full"></span>
                        </span></h3>
</body>
</html>
<script>
    // 从会话存储中获取用户信息
    function getUserInfo() {
        return {
            username: '${session.username}',
            usertype: '${session.usertype}',
            loginTime: '${session.currentTime}'
        };
    }

    // 设置欢迎词中的用户名
    function setWelcomeUsername() {
        const userInfo = getUserInfo();
        if (userInfo && userInfo.username) {
            document.getElementById('welcomeUsername').textContent = userInfo.username;
        } else {
            // 如果未登录，重定向到登录页
            window.location.href = 'login.html';
        }
    }
    // 页面加载完成后执行
    document.addEventListener('DOMContentLoaded', () => {
        setWelcomeUsername();
    });
</script>
