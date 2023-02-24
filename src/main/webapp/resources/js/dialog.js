var jlab = jlab || {};

$(document).on("click", "#helpOpener", function() {
    $("#helpDialog").dialog("open");
    return false; /*Don't update url hash*/
});

$(document).on("click", "#openOpener", function() {
    $("#openDialog").dialog("open");
    return false; /*Don't update url hash*/
});

$(document).on("click", ".dialog-close-button", function() {
    $(this).closest(".dialog").dialog("close");
});

$(document).on("click", ".delete-button", function() {
    return confirm("Are you sure you want to delete the presentation?");
});

$(document).on("click", ".elog-button", function() {
    var input = $("<input type='hidden'/>").attr("name", "action").attr("value", $(this).val());
    $(this).closest("form").append(input);
    $("button, input[type='submit']").button("disable");
    $(this).closest("form").submit();
});

$(function() {
    $(".datepicker").datepicker({
        dateFormat: 'yy-mm-dd',
        showOn: 'both',
        buttonImage: "resources/img/calendar.png"
    });

    $("button, input[type='submit'], .styled-button, #auth a").button();

    $(".dialog").dialog({
        autoOpen: false,
        show: "blind",
        hide: "explode",
        modal: true,
        resizable: false
    });
});
