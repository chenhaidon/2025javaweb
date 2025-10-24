const registerForm = document.getElementById('registerForm');
const usernameInput = document.getElementById('username');
const password1Input = document.getElementById('password1');
const password2Input = document.getElementById('password2');
const phoneInput = document.getElementById('phone');
const genderRadios = document.querySelectorAll('input[name="gender"]');
const hobbyCheckboxes = document.querySelectorAll('input[name="hobby"]');

// 获取所有错误信息 span 元素
const usernameErrorSpan = document.getElementById('usernameError');
const password1ErrorSpan = document.getElementById('password1Error');
const password2ErrorSpan = document.getElementById('password2Error');
const phoneErrorSpan = document.getElementById('phoneError');
const genderErrorSpan = document.getElementById('genderError');
const hobbyErrorSpan = document.getElementById('hobbyError');


const togglePassword1Icon = document.getElementById('togglePassword1'); // 改成 icon
const togglePassword2Icon = document.getElementById('togglePassword2'); // 改成 icon
// 正则表达式
// 用户名：3-15位，字母、数字、下划线
const usernameRegex = /^[a-zA-Z0-9_]{3,15}$/;
// 密码：至少一个大写，一个小写，一个数字，长度6-20
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/;
// 电话号码：中国大陆手机号，11位，以1开头
const phoneRegex = /^1[0-9]{10}$/;

// 清除所有错误信息
function clearErrors() {
    usernameErrorSpan.textContent = '';
    password1ErrorSpan.textContent = '';
    password2ErrorSpan.textContent = '';
    phoneErrorSpan.textContent = '';
    genderErrorSpan.textContent = '';
    hobbyErrorSpan.textContent = '';
}

// 验证用户名
function validateUsername() {
    const username = usernameInput.value.trim();
    if (username === '') {
        usernameErrorSpan.textContent = '用户名不能为空。';
        return false;
    }
    if (!usernameRegex.test(username)) {
        usernameErrorSpan.textContent = '用户名需3-15位，且只能包含字母、数字和下划线。';
        return false;
    }
    usernameErrorSpan.textContent = '';
    return true;
}

// 验证密码
function validatePassword1() {
    const password = password1Input.value;
    if (password === '') {
        password1ErrorSpan.textContent = '密码不能为空。';
        return false;
    }
    if (!passwordRegex.test(password)) {
        password1ErrorSpan.textContent = '密码需6-20位，包含大小写字母和数字。';
        return false;
    }
    password1ErrorSpan.textContent = '';
    return true;
}

// 验证密码确认
function validatePassword2() {
    const password = password1Input.value;
    const password2 = password2Input.value;
    if (password2 === '') {
        password2ErrorSpan.textContent = '请确认密码。';
        return false;
    }
    if (password !== password2) {
        password2ErrorSpan.textContent = '两次输入的密码不一致。';
        return false;
    }
    password2ErrorSpan.textContent = '';
    return true;
}

// 验证电话号码
function validatePhone() {
    const phone = phoneInput.value.trim();
    if (phone === '') {
        phoneErrorSpan.textContent = '电话号码不能为空。';
        return false;
    }
    if (!phoneRegex.test(phone)) {
        phoneErrorSpan.textContent = '请输入11位有效的中国大陆手机号码。';
        return false;
    }
    phoneErrorSpan.textContent = '';
    return true;
}

// 验证性别
function validateGender() {
    let genderSelected = false;
    for (const radio of genderRadios) {
        if (radio.checked) {
            genderSelected = true;
            break;
        }
    }
    if (!genderSelected) {
        genderErrorSpan.textContent = '请选择您的性别。';
        return false;
    }
    return true;
}

// 验证爱好
function validateHobby() {
    let hobbySelected = false;
    for (const checkbox of hobbyCheckboxes) {
        if (checkbox.checked) {
            hobbySelected = true;
            break;
        }
    }
    if (!hobbySelected) {
        hobbyErrorSpan.textContent = '请至少选择一项爱好。';
        return false;
    }
    return true;
}

// 表单提交时的验证
registerForm.addEventListener('submit', function(event) {
    clearErrors(); // 每次提交前先清除之前的错误信息

    let isValid = true; // 假设表单是有效的

    // 依次调用所有验证函数
    if (!validateUsername()) {
        isValid = false;
    }
    if (!validatePassword1()) {
        isValid = false;
    }
    if (!validatePassword2()) {
        isValid = false;
    }
    if (!validatePhone()) {
        isValid = false;
    }
    if (!validateGender()) {
        isValid = false;
    }
    if (!validateHobby()) {
        isValid = false;
    }

    // 如果任何一个验证不通过，就阻止表单提交
    if (!isValid) {
        event.preventDefault();
    } else {
        // 如果所有验证都通过，可以执行一些额外的操作（例如显示成功消息）
        // console.log('表单验证通过，即将提交...');
        alert('注册信息有效，正在提交！'); // 用户体验提示
        // 注意：如果 action="../Register" 是一个服务器端处理的 URL，
        // 那么表单会自动提交，无需再调用 registerForm.submit();
    }
});

// 实时验证（可选，提供更好的用户体验）
usernameInput.addEventListener('input', validateUsername); // 离开焦点时验证
password1Input.addEventListener('blur', validatePassword1);
password2Input.addEventListener('blur', validatePassword2);
phoneInput.addEventListener('input', validatePhone);

// 实时验证密码一致性，当密码输入框输入时就检查
password1Input.addEventListener('input', function() {
    // 当第一个密码框内容改变时，如果第二个密码框有内容，就立即检查它们是否一致
    if (password2Input.value !== '') {
        validatePassword2();
    }
});
password2Input.addEventListener('input', validatePassword2); // 输入时立即验证

// 性别和爱好通常不需要实时验证，因为用户会点击选择
// 但是，如果用户不点击，提交时依然会验证

function togglePasswordWithIcon(passwordInput, toggleIcon) {
    const type = passwordInput.getAttribute('type');

    if (type === 'password') {
        passwordInput.setAttribute('type', 'text');
        // 切换 Font Awesome 图标类
        toggleIcon.classList.remove('fa-eye');
        toggleIcon.classList.add('fa-eye-slash');
    } else {
        passwordInput.setAttribute('type', 'password');
        toggleIcon.classList.remove('fa-eye-slash');
        toggleIcon.classList.add('fa-eye');
    }
}
togglePassword1Icon.addEventListener('click', function() {
    togglePasswordWithIcon(password1Input, togglePassword1Icon);
});

togglePassword2Icon.addEventListener('click', function() {
    togglePasswordWithIcon(password2Input, togglePassword2Icon);
});
password1Input.addEventListener('input', function() {
    if (password1Input.getAttribute('type') === 'text') {
        togglePassword1Icon.classList.remove('fa-eye');
        togglePassword1Icon.classList.add('fa-eye-slash');
    } else {
        togglePassword1Icon.classList.remove('fa-eye-slash');
        togglePassword1Icon.classList.add('fa-eye');
    }
});
password2Input.addEventListener('input', function() {
    if (password2Input.getAttribute('type') === 'text') {
        togglePassword2Icon.classList.remove('fa-eye');
        togglePassword2Icon.classList.add('fa-eye-slash');
    } else {
        togglePassword2Icon.classList.remove('fa-eye-slash');
        togglePassword2Icon.classList.add('fa-eye');
    }
});