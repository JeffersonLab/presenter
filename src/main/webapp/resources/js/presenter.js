var jlab = jlab || {};

jlab.su = function(url) {
    var i = document.createElement('iframe');
    i.style.display = 'none';
    i.onload = function() { i.parentNode.removeChild(i); window.location.href = url; };
    i.src = jlab.logoutUrl;
    document.body.appendChild(i);
};

jlab.login = function(url) {
    var i = document.createElement('iframe');
    i.style.display = 'none';
    i.onload = function() { i.parentNode.removeChild(i); window.location.href = url; };
    i.src = jlab.loginUrl;
    document.body.appendChild(i);
};
$(document).on("click", "#login-link", function () {
    var url = $(this).attr("href");

    jlab.login(url);

    return false;
});
$(document).on("click", "#su-link", function() {
    var url = $(this).attr("href");

    jlab.su(url);

    return false;
});

