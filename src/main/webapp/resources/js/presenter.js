var jlab = jlab || {};

jlab.su = function(url) {
    var i = document.createElement('iframe');
    i.style.display = 'none';
    i.onload = function() { i.parentNode.removeChild(i); window.location.href = url; };
    i.src = jlab.suLogoutUrl;
    document.body.appendChild(i);
};

jlab.iframeLogin = function(url) {
    var i = document.createElement('iframe');
    i.style.display = 'none';
    i.onload = function() { i.parentNode.removeChild(i); window.location.href = url; };
    i.src = jlab.iframeLoginUrl;
    document.body.appendChild(i);
};
$(document).on("click", "#login-link", function () {
    var url = $(this).attr("href");

    if(jlab.iframeLoginUrl !== '') {
        jlab.iframeLogin(url);

        return false;
    } /* else perform standard action (follow link) */
});
$(document).on("click", "#su-link", function() {
    var url = $(this).attr("href");

    jlab.su(url);

    return false;
});

