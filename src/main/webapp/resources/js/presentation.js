var presenter = presenter || {};

/* SCALE PROPERTIES */
presenter.scaleConfig = {
    minHeight: 300,
    minWidth: 400,
    aspectRatio: 0.75,
    viewportId: 'viewport'
};

/* SCALE FUNCTIONS */
presenter.scaleSlide = function () {
    if ($('body').hasClass('iframe-selected')) {
        presenter.transformSlide();
    } else {
        presenter.sizeSlideBox();
        presenter.scaleFont();
    }
};

presenter.transformSlide = function () {

    var $viewport = $('#' + presenter.scaleConfig.viewportId);
    var $scalable = $('#' + presenter.scaleConfig.viewportId + ' > .slide');

    var slideWidth = $viewport.width();
    var slideHeight = $viewport.height();

    if (slideHeight < presenter.scaleConfig.minHeight) {
        slideHeight = presenter.scaleConfig.minHeight;
    }

    if (slideWidth < presenter.scaleConfig.minWidth) {
        slideWidth = presenter.scaleConfig.minWidth;
    }

    if (slideHeight < (slideWidth * presenter.scaleConfig.aspectRatio)) {
        /*Compute width from height*/
        slideWidth = slideHeight * (1.0 / presenter.scaleConfig.aspectRatio);
    } else {
        /*Compute height from width*/
        slideHeight = slideWidth * presenter.scaleConfig.aspectRatio;
    }

    var currentSlideWidth = $scalable.width();

    var scaleFactor = slideWidth / currentSlideWidth;

    $scalable.css("-moz-transform-origin", "center 0");
    $scalable.css("-moz-transform", "scale(" + scaleFactor + ")");
    $scalable.css("-ms-transform-origin", "center 0");
    $scalable.css("-ms-transform", "scale(" + scaleFactor + ")");
    $scalable.css("-webkit-transform-origin", "center 0");
    $scalable.css("-webkit-transform", "scale(" + scaleFactor + ")");
    $scalable.css("-o-transform-origin", "center 0");
    $scalable.css("-o-transform", "scale(" + scaleFactor + ")");
    $scalable.css("transform-origin", "center 0");
    $scalable.css("transform", "scale(" + scaleFactor + ")");
};

presenter.sizeSlideBox = function () {

    var viewport = document.getElementById(presenter.scaleConfig.viewportId);
    var scalable = document.querySelector('#' + presenter.scaleConfig.viewportId + ' > .slide');

    if (!scalable) {
        return; /*Nothing in viewport*/
    }

    var viewportWidth = viewport.offsetWidth;
    var viewportHeight = viewport.offsetHeight;
    var slideMargin = 0;

    var slideWidth = (viewportWidth - (slideMargin * 2.0));
    var slideHeight = (viewportHeight - (slideMargin * 2.0));

    if (slideHeight < presenter.scaleConfig.minHeight) {
        slideHeight = presenter.scaleConfig.minHeight;
    }

    if (slideWidth < presenter.scaleConfig.minWidth) {
        slideWidth = presenter.scaleConfig.minWidth;
    }

    if (slideHeight < (slideWidth * presenter.scaleConfig.aspectRatio)) {
        /*Compute width from height*/
        slideWidth = slideHeight * (1.0 / presenter.scaleConfig.aspectRatio);
    } else {
        /*Compute height from width*/
        slideHeight = slideWidth * presenter.scaleConfig.aspectRatio;
    }

    scalable.style.width = slideWidth + "px";
    scalable.style.height = slideHeight + "px";
};

presenter.scaleFont = function () {
    var scalable = document.querySelector('#' + presenter.scaleConfig.viewportId + ' > .slide');

    if (!scalable) {
        return; /*Nothing in viewport*/
    }

    var slideWidth = scalable.offsetWidth;
    var fontFractionOfWidth = 0.333;
    var scaleFactor = presenter.scaleConfig.aspectRatio * fontFractionOfWidth;
    var fontSize = slideWidth * scaleFactor;
    scalable.style.fontSize = fontSize + "%";
};

//presenter.keepAliveTimeoutMillis = 15000;
presenter.keepAliveTimeoutMillis = 120000;
/*presenter.keepAliveTimeoutMillis = 1500000;*/

presenter.initializeKeepAlive = function () {
    setTimeout("presenter.doKeepAlive()", presenter.keepAliveTimeoutMillis);
};

$(document).on("click", ".close-change-warning", function () {
    $(".change-warning-panel").hide("slide", {direction: "down"}, 1000);
});

$(document).on("click", ".modified-reload", function () {
    window.location.reload();
    return false;
});

$(document).on("click", ".modified-cc-reload", function () {
    $("#preceding-day-count").val(''); // blank means anonymous refresh
    $("#submit-save-refresh-shift-logs").click();
    return false;
});

$(document).on("click", ".modified-pd-reload", function () {
    $("#preceding-day-count").val(''); // blank means anonymous refresh
    $("#submit-save-refresh-shift-logs").click();
    return false;
});

presenter.triCharMonthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

presenter.dateTimeToJLabString = function (x) {
    var year = x.getFullYear(),
            month = x.getMonth(),
            day = x.getDate(),
            hour = x.getHours(),
            minute = x.getMinutes(),
            second = x.getSeconds();

    return presenter.pad(day, 2) + '-' + presenter.triCharMonthNames[month] + '-' + year + ' ' + presenter.pad(hour, 2) + ':' + presenter.pad(minute, 2) + ':' + presenter.pad(second, 2);
};

presenter.updateLastModified = function (user, lastModifiedMillis) {
    window.console && console.log('updated last modified to: ' + presenter.dateTimeToJLabString(new Date(lastModifiedMillis * 1)));
    presenter.auditMap[presenter.presentationId] = lastModifiedMillis * 1;
    $("#save-button").attr("title", "Last Modified " + presenter.dateTimeToJLabString(new Date(lastModifiedMillis * 1)) + " by " + user);
};

presenter.popAsyncChangeWarning = function (user, lastModifiedMillis, synced, description, change) {
    $(".change-warning-user").text(user);
    $(".change-warning-description").text(description);
    $(".change-warning-last-modified").text(presenter.dateTimeToJLabString(new Date(lastModifiedMillis * 1)));

    $(".modified-cc-reload").hide();
    $(".modified-pd-reload").hide();
    $(".modified-reload").hide();

    if (synced) {
        if (presenter.sync === 'CC') {
            $(".modified-cc-reload").show();
        } else if (presenter.sync === 'PD') {
            $(".modified-pd-reload").show();
        } else {
            $(".modified-reload").show();
        }
    } else {
        $(".modified-reload").show();
    }

    $(".change-warning-panel").show("slide", {direction: "down"}, 1000);
};

presenter.doKeepAlive = function () {
    /*If we are in the middle of a request already just re-schedule for next time*/
    if (presenter.ajaxInProgress) {
        setTimeout("presenter.doKeepAlive()", presenter.keepAliveTimeoutMillis);
        return;
    }

    var request = jQuery.ajax({
        url: presenter.ctx + "/keep-alive",
        type: "GET",
        data: {presentationId: presenter.presentationId},
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Your session has expired.  Please copy any unsaved work to the system clipboard or try the save button before reloading the page and re-login to continue.');
        } else {
            setTimeout("presenter.doKeepAlive()", presenter.keepAliveTimeoutMillis);

            var newMap = {},
                    addedMap = {},
                    originalMap = {};

            // First make a copy of auditMap as we'll be removing things that are missing
            for (var key in presenter.auditMap) {
                originalMap[key] = presenter.auditMap[key];
            }

            // Put data from server into map
            $(".presentation", data).each(function (index) {
                var $newAudit = $(this),
                        lastModifiedMillis = $newAudit.attr("data-last-modified-millis"),
                        user = $newAudit.attr("data-user"),
                        id = $newAudit.attr("data-id"),
                        description = $newAudit.attr("data-description");

                newMap[id] = {modified: lastModifiedMillis, user: user, description: description};
            });

            // PART A: Update auditMap to match data from server
            presenter.auditMap = {};
            for (id in newMap) {
                presenter.auditMap[id] = newMap[id].modified * 1;
            }

            // PART B: Handle changes to presentation itself
            if (presenter.presentationId in newMap) {
                //window.console && console.log('Checking Core presentation for changes');

                var newLastModified = newMap[presenter.presentationId].modified * 1,
                        description = newMap[presenter.presentationId].description;

                if (newLastModified !== originalMap[presenter.presentationId]) {
                    //window.console && console.log('new last modified: ' + presenter.dateTimeToJLabString(new Date(newLastModified)));
                    //window.console && console.log('old last modified: ' + presenter.dateTimeToJLabString(new Date(originalMap[presenter.presentationId])));
                    //presenter.auditMap[presenter.presentationId] = newLastModified;
                    presenter.popAsyncChangeWarning(newMap[presenter.presentationId].user, newLastModified, false, description, 'modified');
                    return;
                } else {
                    //window.console && console.log('No changes found');
                }

                delete newMap[presenter.presentationId]; // now all that remains is synced presentations for part B
            } else {
                //window.console && console.log('Core presentation audit missing');
                return;
            }

            // PART C: Handle changes to synced presentations
            //window.console && console.log('Checking Synced presentatations for changes');
            for (var id in newMap) {
                if (id in originalMap) {
                    //window.console && console.log('Matching synced presentation found with ID: ' + id);

                    var newLastModified = newMap[id].modified * 1,
                            description = newMap[id].description;

                    if (newLastModified !== originalMap[id]) {
                        //window.console && console.log('new last modified: ' + presenter.dateTimeToJLabString(new Date(newLastModified)));
                        //window.console && console.log('old last modified: ' + presenter.dateTimeToJLabString(new Date(originalMap[id])));
                        //presenter.auditMap[id] = newLastModified;
                        presenter.popAsyncChangeWarning(newMap[id].user, newLastModified, true, description, 'modified');
                        return;
                    } else {
                        //window.console && console.log('No changes found');
                    }

                    delete originalMap[id];
                } else {
                    //window.console && console.log('Found newly added presentation');
                    addedMap[id] = newMap[id];
                }

                /*window.console && console.log(lastModifiedMillis + " vs " + presenter.lastModifiedMillis);*/
            }

            // PART D: Handle new synced presentations - we don't worry about this as it is rare that a CC will wait until the last second to even create their shift logs - the audit mechanism on server doesn't even check for missing synced presentations right now
            //window.console && console.log('Handling newly added synced presentatations');
            // addedMap
            /*for (id in addedMap) { // just grab first one
             window.console && console.log('New synced presentation found with ID: ' + id);
             var newLastModified = addedMap[id].modified * 1,
             description = newMap[id].description;
             
             presenter.popAsyncChangeWarning(addedMap[id].user, newLastModified, true, description, 'newly added');
             return;
             }*/

            // PART E: Handle removed synced presentations - this list should always be empty - even if someone reduces preceding days (e.g. from 3 to 1) the presentation itself will be modified
            //window.console && console.log('Handling newly removed synced presentatations');
            // originalMap

        }
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to communicate with server: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to communicate with server');

    });
};

presenter.initializeViewportScale = function () {
    presenter.scaleSlide();
};



/* FULL SCREEN FUNCTIONS */
presenter.enableFullScreen = function () {
    if (document.documentElement.requestFullScreen) {
        document.documentElement.requestFullScreen();
    } else if (document.documentElement.mozRequestFullScreen) {
        document.documentElement.mozRequestFullScreen();
    } else if (document.documentElement.webkitRequestFullScreen) {
        document.documentElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
    }

    $("body").addClass("fullscreen");
    presenter.disableSlideEditor();
    $(".aloha-cleanme").remove();
    presenter.scaleSlide();
    $("#full-screen-button").text("End Show");
};

presenter.disableFullScreen = function () {
    /*Dynamic two columns need to be hidden, then shown to force relayout in FF when in read-only mode*/
    $("#viewport .slide .editable.dynamic-two-columns").hide();

    if (document.cancelFullScreen) {
        document.cancelFullScreen();
    } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if (document.webkitCancelFullScreen) {
        document.webkitCancelFullScreen();
    }

    $("body").removeClass("fullscreen");
    presenter.enableSlideEditor();
    presenter.scaleSlide();
    $("#full-screen-button").text("Full Screen");

    $("#laser-pointer-overlay").hide();
    $("#laser-pointer-button").text("Laser On");

    $("#viewport .slide .editable.dynamic-two-columns").show();
};

presenter.toggleFullScreen = function () {
    if ($("body").hasClass("fullscreen")) {
        presenter.disableFullScreen();
    } else {
        presenter.enableFullScreen();
    }
};

presenter.isActuallyFullScreen = function () {
    return !((document.fullScreenElement && document.fullScreenElement !== null)
            || (!document.mozFullScreen && !document.webkitIsFullScreen));
};

presenter.verifyFullScreenState = function () {
    if (!presenter.isActuallyFullScreen()) {
        if ($("body").hasClass("fullscreen")) {
            $("body").removeClass("fullscreen");
            presenter.enableSlideEditor();
            presenter.scaleSlide();
            $("#full-screen-button").text("Full Screen");
            $("#laser-pointer-overlay").hide();
            $("#laser-pointer-button").text("Laser On");
        }
    }
};




/* Slide Editor (WYSIWYG) */
presenter.enableSlideEditor = function () {
    if (presenter.editable) {
        Aloha.jQuery('#viewport .slide:not(.synced-slide) .editable').aloha();
    }
};

presenter.disableSlideEditor = function () {
    Aloha.jQuery('#viewport .editable').mahalo();
};








/* PLACEHOLDER FUNCTIONS */
presenter.updatePlaceholder = function (force) {
    var $parent = $(this),
            $editable = $parent.find(".editable"),
            $img = $parent.find(".img-div"),
            $iframe = $parent.find("iframe");

    if (force === true) {
        $parent['addClass']('filled');
    } else if ($editable.length > 0) {
        $parent[$editable.text() ? 'addClass' : 'removeClass']('filled');
    } else if ($img.length > 0) {
        $parent[($img.css("background-image") !== "url('data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7')") ? 'addClass' : 'removeClass']('filled');
    } else if ($iframe.length > 0) {
        $parent[($iframe.attr("src") !== "about:blank") ? 'addClass' : 'removeClass']('filled');
    }

    return $parent;
};

presenter.editableKeydown = function (e) {
    var c = e.keyCode;
    ((47 < c && c < 91) || (95 < c && c < 112) || (185 < c && c < 223)) && presenter.updatePlaceholder.call(this, true);
};

presenter.initializePlaceholders = function () {
    $(".input-wrapper").each(function () {
        presenter.updatePlaceholder.call(this);
    });
};





/*AJAX REQUEST INDICATOR*/
presenter.startRequest = function () {
    presenter.ajaxInProgress = true;
    $("#save-button").html($(".indicator16x16").clone().removeClass("indicator16x16"));
};

presenter.endRequest = function () {
    presenter.ajaxInProgress = false;
    $("#save-button").html("Saved");
};





/* NEW SLIDE FUNCTIONS */
presenter.newSlide = function ($template) {
    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.startRequest();

    var $slide = $template.clone();
    $slide.removeAttr("id");
    var data = presenter.getSlideData($slide);
    data['presentationId'] = presenter.presentationId;

    var request = jQuery.ajax({
        url: presenter.ctx + "/new-slide",
        type: "POST",
        data: data,
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to create new slide: ' + $(".reason", data).html());
        } else {
            $slide.find(".slide").attr("data-slideid", $(".slideId", data).html());
            $slide.find(".slide-number").text($("#slide-list > li").length + 1);

            $("#slide-list").append($slide);

            presenter.setSelected($("#slide-list > li:last"));

            $("#slide-list").animate({
                scrollTop: $("#slide-list").prop("scrollHeight") - $("#slide-list").height()
            }, 'slow');

            var lastModifiedMillis = $(".presentation", data).attr("data-last-modified-millis"),
                    user = $(".presentation", data).attr("data-user");

            presenter.updateLastModified(user, lastModifiedMillis);
        }
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to save new slide: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to save new slide');

    });

    request.always(function () {
        presenter.endRequest();
    });
};

presenter.afterLoginAction = null;

presenter.initializeDatepickerDialog = function () {
    $("#datepicker-dialog").dialog({
        autoOpen: false,
        modal: true,
        resizable: false,
        draggable: false,
        width: 400,
        height: 375
    });
};

presenter.initializeHelpDialog = function () {
    $("#helpDialog").dialog({
        autoOpen: false,
        modal: true,
        resizable: false,
        draggable: false,
        width: 300,
        height: 400
    });
};

presenter.initializeNewSlideDialog = function () {
    $("#new-slide-dialog").dialog({
        autoOpen: false,
        modal: true,
        resizable: false,
        draggable: false,
        width: 975,
        height: 600
    });
};

presenter.initializeImportShiftLogDialog = function () {
    $("#import-shift-logs-dialog").dialog({
        autoOpen: false,
        modal: true,
        resizable: false,
        draggable: false,
        width: 400,
        height: 300
    });
};

presenter.initializeElogSuccessDialog = function () {
    $("#success-dialog").dialog({
        autoOpen: false,
        modal: true,
        resizable: false,
        draggable: false,
        width: 400,
        height: 300
    });
};

presenter.initializeLaser = function () {
    presenter.svg = document.getElementById("laser-pointer-surface");
    presenter.spot = document.getElementById("laser-spot");
    presenter.point = presenter.svg.createSVGPoint();
};

$(document).on("mousemove", function (event) {
    if (!$("#laser-pointer-overlay").is(":hidden")) {
        var cursorLocation = presenter.cursorLocation(event);

        var clone = $("#laser-spot").clone(),
                skip = true;

        clone.removeAttr("id").appendTo("#laser-pointer-surface").fadeOut({
            always: function () {
                clone.remove();
            },
            step: function () {
                if (skip) {
                    skip = false;
                } else {
                    var r = clone.attr("r");
                    clone.attr("r", r * 0.75);
                    skip = true;
                }
            }
        });

        presenter.spot.setAttribute("cx", cursorLocation.x);
        presenter.spot.setAttribute("cy", cursorLocation.y);
    }
});

$(document).on("click", "#laser-pointer-button", function () {
    $("#laser-pointer-overlay").toggle();

    if ($("#laser-pointer-overlay").is(":hidden")) {
        $("#laser-pointer-button").text("Laser On");
    } else {
        $("#laser-pointer-button").text("Laser Off");
    }
});

presenter.cursorLocation = function (event) {
    presenter.point.x = event.clientX;
    presenter.point.y = event.clientY;
    return presenter.point.matrixTransform(presenter.svg.getScreenCTM().inverse());
};

/* SLIDE SORTER AND VIEWPORT FUNCTIONS */
presenter.handleUserKeyPress = function (e) {
    /**
     * PowerPoint remotes send F5 key for fullscreen; MCC remote 
     * sends f5 everytime you activate the laser pointer!
     * Therefore we prevent propagation to avoid browser refresh
     * and otherwise ignore
     */
    if (e.keyCode === 116) {
        e.stopPropagation();
        e.preventDefault();
        return false;
    }

    /**
     * Casually prevent scaling with Ctrl+ or Ctrl-.  This only works in some
     * browsers and is easy to circumvent by using the view menu or Ctrl_ / 
     * Ctrl= aliases.
     */
    if (e.ctrlKey && (e.keyCode === 107 || e.keyCode === 109)) {
        e.stopPropagation();
        e.preventDefault();
        return false;
    }

    /* Only listen to keydown if Aloha editor isn't active or login dialog isn't open */
    if (Aloha.activeEditable === null && !$("#login-dialog").is(":visible")) {
        if (e.keyCode === 70) {
            presenter.toggleFullScreen();
        } else if (e.keyCode === 39 || e.keyCode === 78 || e.keyCode === 34) {
            presenter.nextSlide();
        } else if (e.keyCode === 37 || e.keyCode === 80 || e.keyCode === 33) {
            presenter.previousSlide();
        } else if (e.keyCode === 8 && ($("#import-shift-logs-dialog").dialog("isOpen") === false)) {
            /*Prevent backspace key from acting as back-button since users will
             *often hit it without realizing they're not focused on input;  note
             *javascript prompt doesn't appear to be adversly affected.
             *However, must allow backspace when import-shift-logs-dialog is open*/
            e.preventDefault();
            e.stopPropagation();
            return false;
        }
    }

    if (e.keyCode === 13 && $("#login-dialog").is(":visible")) {
        presenter.doLogin();
    }

    return true;
};

presenter.doSlideChange = function ($item) {
    $("#slide-list > li").removeClass("selected-list-item");

    if ($item.length > 0) {

        $item.addClass("selected-list-item");
        var viewportslide = $item.find(".slide").clone();
        $(viewportslide).removeAttr("id");
        $("#viewport").empty();
        $("#viewport").append(viewportslide);

        $("body").removeClass("iframe-selected image-selected accesses-log-selected week-log-selected shift-log-selected synced-slide-selected");

        if (viewportslide.find("iframe").length > 0) {
            $("body").addClass("iframe-selected");
        }

        if (viewportslide.find(".image-container").length > 0) {
            $("body").addClass("image-selected");
        }

        if (viewportslide.find(".bta-table").length > 0) {
            $("body").addClass("shift-log-selected");
        }

        if (viewportslide.find(".beam-to-halls-table").length > 0) {
            $("body").addClass("week-log-selected");
        }

        if (viewportslide.find(".access-times-table").length > 0) {
            $("body").addClass("accesses-log-selected");
        }

        if (viewportslide.hasClass("synced-slide")) {
            $("body").addClass("synced-slide-selected");
        }

        presenter.scaleSlide();

        if (!$("body").hasClass("fullscreen")) {
            presenter.enableSlideEditor();
        }

        if (presenter.hasNextSlide()) {
            $("#next-button").removeAttr("disabled");
        } else {
            $("#next-button").attr("disabled", "disabled");
        }

        if (presenter.hasPreviousSlide()) {
            $("#previous-button").removeAttr("disabled");
        } else {
            $("#previous-button").attr("disabled", "disabled");
        }
    } else {
        $("#viewport").empty();
        $("body").removeClass("iframe-selected image-selected accesses-log-selected week-log-selected shift-log-selected");
        $("#next-button").attr("disabled", "disabled");
        $("#previous-button").attr("disabled", "disabled");
    }
};

presenter.setSelected = function ($item) {

    var slidenum = $item.find(".slide-number").text();
    window.location.replace("#" + parseInt(slidenum));

    presenter.doSlideChange($item);
};

presenter.hasNextSlide = function () {
    var $next = $(".selected-list-item").next("li");
    return ($next.length > 0);
};

presenter.nextSlide = function () {
    var $next = $(".selected-list-item").next("li");
    if ($next.length > 0) {
        presenter.setSelected($next);
    }
};

presenter.hasPreviousSlide = function () {
    var $previous = $(".selected-list-item").prev("li");
    return ($previous.length > 0);
};

presenter.previousSlide = function () {
    var $previous = $(".selected-list-item").prev("li");
    if ($previous.length > 0) {
        presenter.setSelected($previous);
    }
};

presenter.checkFocus = function () {
    if (document.activeElement == document.querySelector("#viewport iframe")) {
        $("body").addClass("iframe-focused");
    } else {
        $("body").removeClass("iframe-focused");
    }
};

presenter.checkOverflow = function () {
    var $slide = $("#viewport .slide");

    /*IE thinks iframes are always overflowed*/
    if ($("body").hasClass("iframe-selected")) {
        $("body").removeClass("out-of-bounds");
        return;
    }

    if ($slide.length > 0) {
        var contentHeight = $slide[0].scrollHeight,
                boundsHeight = $slide[0].clientHeight,
                contentWidth = $slide[0].scrollWidth,
                boundsWidth = $slide[0].clientWidth;

        if ((contentHeight > boundsHeight) || (contentWidth > boundsWidth)) {
            $("body").addClass("out-of-bounds");
        } else {
            $("body").removeClass("out-of-bounds");
        }
    } else {
        $("body").removeClass("out-of-bounds");
    }
};

presenter.handlePollChecks = function () {
    presenter.checkFocus();
    presenter.checkOverflow();
};

presenter.initializePollChecks = function () {
    window.setInterval(presenter.handlePollChecks, 1000);
};

presenter.initializeSlideSorter = function () {
    if (presenter.editable) {
        $("#slide-list").sortable({
            stop: function () {
                presenter.order();
            }
        });
    }

    $("#slide-list").attr('unselectable', 'on').css('user-select', 'none').on('selectstart', false);

    presenter.doSyncSlideGroupOrder();
};

presenter.deleteSlide = function () {
    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.startRequest();

    var needsReorder = false;

    if (confirm('Are you sure you want to delete?')) {
        var slideId = $(this).prevAll(".slide").attr("data-slideid");
        var $item = $(this).parent("li");
        var request = jQuery.ajax({
            url: presenter.ctx + "/delete-slide",
            type: "POST",
            data: {
                slideId: slideId
            },
            dataType: "html"
        });

        request.done(function (data) {
            if ($(".status", data).html() !== "Success") {
                alert('Unable to delete slide: ' + $(".reason", data).html());
            } else { // TODO: Must also return zero or one presentations that are now no longer associated so we can update auditMap so user who did delete doesn't get change notification (though any other users, even anonymous, doing load will put it back if a required sync slide!).  Not sure if worth implementing as users aren't supposed to remove required synced slides
                $item.remove();
                needsReorder = true;

                var lastModifiedMillis = $(".presentation", data).attr("data-last-modified-millis"),
                        user = $(".presentation", data).attr("data-user");

                presenter.updateLastModified(user, lastModifiedMillis);
            }
        });

        request.error(function (xhr, textStatus) {
            window.console && console.log('Unable to delete slide: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
            alert('Unable to delete slide');

        });

        request.always(function () {
            presenter.endRequest();

            if (needsReorder) {
                presenter.order();
                if ($(".selected-list-item").length < 1) {
                    presenter.setSelected($("#slide-list > li:first"));
                }
            }
        });
    } else {
        presenter.endRequest();
    }
};

presenter.doSyncSlideGroupOrder = function () {
    /*We only do synced slide grouping for certiain presentations*/
    if (presenter.presentationSubType !== 'RUN' &&
            presenter.presentationSubType !== 'SAM' &&
            presenter.presentationSubType !== 'HCO') {
        return;
    }

    var slidesPerPresentation = {};

    $("#slide-list .synced-slide .group-order").remove();

    $("#slide-list .synced-slide").each(function () {
        var key = $(this).attr("data-sync-presentationid");
        if (slidesPerPresentation[key] === undefined) {
            slidesPerPresentation[key] = 1;
        } else {
            slidesPerPresentation[key]++;
        }
    });

    $.each(slidesPerPresentation, function (name, value) {
        if (value > 1) {
            $('#slide-list .synced-slide[data-sync-presentationid="' + name + '"]').each(function (j) {
                $(this).append('<div class="group-order"><div>Page ' + (j + 1) + ' of ' + value + '</div></div>');
            });
        }
    });

    // Refresh group order in viewport
    var selectedLi = $("#slide-list .selected-list-item");
    if (selectedLi.length > 0) {
        presenter.setSelected(selectedLi);
    }
};

presenter.order = function () {
    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.startRequest();

    var params = {};
    var count = 0;
    $("#slide-list > li").each(function (index, li) {
        $(li).find(".slide-number").text(index + 1);
        params['slide[' + index + ']'] = $(li).find(".slide").attr("data-slideid");
        count++;
    });

    params['numSlides'] = count;
    params['presentationId'] = presenter.presentationId;

    var request = jQuery.ajax({
        url: presenter.ctx + "/save-slide-order",
        type: "POST",
        data: params,
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to save new slide order: ' + $(".reason", data).html());
        } else {
            var lastModifiedMillis = $(".presentation", data).attr("data-last-modified-millis"),
                    user = $(".presentation", data).attr("data-user");

            presenter.updateLastModified(user, lastModifiedMillis);
            presenter.doSyncSlideGroupOrder();
        }
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to save new slide order: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to save new slide order');

    });

    request.always(function () {
        presenter.endRequest();
    });
};

presenter.getSlideData = function ($slide) {
    var slideId = $slide.find(".slide").attr("data-slideid");
    var slideType = $slide.find(".slide").attr("data-type");
    var slideSubType = $slide.find(".slide").attr("data-subtype");
    var label = $slide.find(".slide-label").text();

    var title = $slide.find(".title").text();
    var body = $slide.find(".body").html();

    var image = $slide.find(".img-div").css("background-image");
    if (image) {
        image = image.replace("url(", "").replace(")", "").replace(/'/g, "").replace(/"/g, "");
    }

    var iframe = $slide.find("iframe").attr("src");
    var date = $slide.find(".date-variable").text();
    var shift = $slide.find(".shift-variable").text();
    var team = $slide.find(".team-variable").text();
    var program = $slide.find(".program-variable").text();
    var from = $slide.find(".from-variable").text();
    var to = $slide.find(".to-variable").text();

    var bta = [];
    $slide.find('.bta-table tr').each(function (i, tr) {
        var myTr = [];

        $('td', tr).each(function (i, td) {
            myTr.push($(td).find(".table-field").text());
        });

        bta.push(myTr);
    });

    var beam = [];
    $slide.find('.beam-to-halls-table tr').each(function (i, tr) {
        var myTr = [];

        $('td', tr).each(function (i, td) {
            myTr.push($(td).find(".table-field").text());
        });

        beam.push(myTr);
    });

    var activity = [];
    $slide.find('.accelerator-activities-table tr').each(function (i, tr) {
        var myTr = [];

        $('td', tr).each(function (i, td) {
            myTr.push($(td).find(".table-field").text());
        });

        activity.push(myTr);
    });


    var access = [];
    $slide.find('.access-times-table tr').each(function (i, tr) {
        if ($(tr).hasClass('total-row')) {
            return;
        }

        var myTr = [];

        $('td', tr).each(function (i, td) {
            myTr.push($(td).find(".table-field").text());
        });

        access.push(myTr);
    });

    return {
        slideId: slideId,
        slideType: slideType,
        slideSubType: slideSubType,
        label: label,
        title: title,
        body: body,
        image: image,
        iframe: iframe,
        date: date,
        shift: shift,
        team: team,
        program: program,
        from: from,
        to: to,
        bta: bta,
        beam: beam,
        activity: activity,
        access: access
    };
};

presenter.getCleanedViewportSlide = function () {
    var $cleanedSlide = $("#viewport").clone();

    $cleanedSlide.find(".slide").removeAttr("style");

    $("div, span", $cleanedSlide).removeClass("aloha-editable aloha-block-blocklevel-sortable ui-sortable aloha-editable-active").removeAttr("id contenteditable");
    $(".aloha-cleanme", $cleanedSlide).remove();

    return $cleanedSlide;
};

//Display a piece of another page in a dialog
presenter.openPageInDialog = function (href, title) {
    $("<div class=\"page-dialog\"></div>").append($('<iframe style="display: block; width: 660px; height: 425px; margin: 0 auto;"/>').attr("src", href))
        .dialog({
            autoOpen: true,
            title: title,
            width: 700,
            height: 500,
            minWidth: 700,
            minHeight: 500,
            resizable: false,
            close: function () {
                $(this).dialog('destroy').remove();
            }
        });
};

presenter.saveSlide = function () {

    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.startRequest();

    var $slide = presenter.getCleanedViewportSlide();

    $(".selected-list-item .slide").replaceWith($slide.html());

    var request = jQuery.ajax({
        url: presenter.ctx + "/save-slide",
        type: "POST",
        data: presenter.getSlideData($slide),
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            if ($(".code", data).text() === 'SESSION_EXPIRED' || $(".code", data).text() === 'NOT_AUTHORIZED') {
                window.open(presenter.ctx + "/relogin", '_blank').focus();
                //presenter.openPageInDialog(presenter.ctx + "/relogin", "Your session has expired");
            } else {
                alert('Unable to save slide: ' + $(".reason", data).html());
            }
        } else {
            var lastModifiedMillis = $(".presentation", data).attr("data-last-modified-millis"),
                    user = $(".presentation", data).attr("data-user");

            presenter.updateLastModified(user, lastModifiedMillis);
        }
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to save slide: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to edit slide');

    });

    request.always(function () {
        presenter.endRequest();
    });
};

presenter.resizeAndInsertImage = function (file) {
    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.startRequest();

    var needsSave = false;

    var img = document.createElement("div");

    img.className = 'img-div';

    var fd = new FormData();

    fd.append("file", file);
    fd.append("width", 1280);
    fd.append("height", 1024);
    fd.append("output-data-uri", "on");
    fd.append("force-png", "on");

    var request = $.ajax({
        url: presenter.resizeUrl,
        type: "POST",
        data: fd,
        cache: false,
        processData: false,
        contentType: false,
        dataType: "text"
    });

    request.done(function (data) {
        $(img).css("background-image", "url('" + data + "')");

        var parent = document.querySelector("#viewport .image-container");
        if (parent) {
            while (parent.hasChildNodes()) {
                parent.removeChild(parent.lastChild);
            }
            parent.appendChild(img);
        }

        needsSave = true;
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to resize image: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to resize image');

    });

    request.always(function () {
        presenter.endRequest();

        if (needsSave) {
            presenter.saveSlide();
        }
    });
};

presenter.handleFiles = function (files) {

    if (files.length !== 1) {
        alert('Only a single file is allowed.');
        return;
    }

    for (var i = 0; i < files.length; i++) {
        var file = files[i];
        var imageType = /image.*/;

        if (!file.type.match(imageType)) {
            alert('Only images files are allowed.');
            continue;
        }

        var fileSizeLimit = 5242880; // 5MB (server should accept POST of 6MB to allow room for other html on slide and also since base64 encoding increases size of image)

        if (file.size > fileSizeLimit) {
            alert('File of size ' + file.size + ' bytes too big; max of ' + fileSizeLimit + ' bytes allowed.');
            continue;
        }

        presenter.resizeAndInsertImage(file);
    }
};

presenter.updateEmbeddedFrame = function () {
    var url = prompt("Enter URL", $("#viewport iframe").attr("src"));

    if (url) {
        /*$("#viewport iframe").attr("src", url);*/
        /*$("#slide-list .selected-list-item iframe").attr("src", url);*/

        var iframe = document.createElement("iframe");
        iframe.src = url;
        var parent = document.querySelector("#viewport .slide");
        if (parent) {
            while (parent.hasChildNodes()) {
                parent.removeChild(parent.lastChild);
            }
            parent.appendChild(iframe);

            presenter.saveSlide();
        }

        /*Copy to SlideList*/
        parent = document.querySelector("#slide-list .selected-list-item .slide");
        if (parent) {
            while (parent.hasChildNodes()) {
                parent.removeChild(parent.lastChild);
            }
            var cloneiframe = $(iframe).clone()[0];
            parent.appendChild(cloneiframe);
        }
    }
};

presenter.openEmbeddedFrameInNewWindow = function () {
    window.open($("#viewport .slide iframe").attr("src"));
};

presenter.initializeHashState = function () {
    /*If Hash present then select that slide, otherwise select first slide and set hash*/
    var slidenum = window.location.hash.substr(1);
    var $slideItem = $("#slide-list > li:eq(" + (slidenum - 1) + ")");
    if ($slideItem.length === 0) {
        $slideItem = $("#slide-list > li:first");
        presenter.setSelected($slideItem);
    } else {
        presenter.doSlideChange($slideItem);
    }
};



/* String Date format: YYYY-MM-DD */
presenter.dateFromIsoString = function (x) {
    var year = parseInt(x.substring(0, 4)),
            month = parseInt(x.substring(5, 7)),
            day = parseInt(x.substring(8, 10));

    return new Date(year, month - 1, day, 0, 0);
};

presenter.dateToIsoString = function (x) {
    var year = x.getFullYear(),
            month = x.getMonth() + 1,
            day = x.getDate();

    return year + '-' + presenter.pad(month, 2) + '-' + presenter.pad(day, 2);
};

presenter.pad = function (n, width, z) {
    z = z || '0';
    n = n + '';
    return n.length >= width ? n : new Array(width - n.length + 1).join(z) + n;
};


/* CREW CHIEF FUNCTIONS */
presenter.updateShiftInfo = function () {

    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.startRequest();

    if (confirm('Are you sure you want to overwrite the shift info (TEAM, PROGRAM, and BTA)?')) {

        var needsSave = false;

        var date = $("#viewport .date-variable").attr("data-date");
        var shift = $("#viewport .shift-variable").attr("data-shift");
        var startHour = 15;

        if (shift === 'OWL') {
            startHour = 23;

            var d = presenter.dateFromIsoString(date);
            d.setHours(0);
            d.setHours(-1); /*Subtract one day*/
            date = presenter.dateToIsoString(d);

        } else if (shift === 'DAY') {
            startHour = 7;
        }

        var shiftStart = date + 'T' + presenter.pad(startHour, 2);
        var $tbody = $("#viewport .bta-table tbody");
        var $team = $("#viewport .team-variable");
        var $program = $("#viewport .program-variable");

        var request = jQuery.ajax({
            url: "/btm/rest/shift-summary",
            type: "GET",
            data: {
                'shift-start': shiftStart
            },
            dataType: "json"
        });

        request.done(function (json) {
            presenter.disableSlideEditor();
            $(".aloha-cleanme").remove();
            $tbody.empty();

            var studies = json['acc-studies-seconds'],
                    studiesPlan = json['plan-studies-seconds'];
            if (studies > 0 || studiesPlan > 0) {
                presenter.doShiftAccRowAdd('Acc. Beam Studies', studies, studiesPlan);
            }

            var restore = json['acc-restore-seconds'],
                    restorePlan = json['plan-restore-seconds'];
            if (restore > 0 || restorePlan > 0) {
                presenter.doShiftAccRowAdd('Acc. Restoration', restore, restorePlan);
            }

            var acc = json['acc-acc-seconds'],
                    accPlan = json['plan-acc-seconds'];
            if (acc > 0 || accPlan > 0) {
                presenter.doShiftAccRowAdd('Acc. Config. Change', acc, accPlan);
            }

            /*var down = json['acc-down-seconds'],
             downPlan = json['plan-down-seconds'];
             if (down > 0 || downPlan > 0) {
             presenter.doShiftAccRowAdd('Down', down, downPlan);
             }*/

            var sad = json['acc-sad-seconds'],
                    sadPlan = json['plan-sad-seconds'];
            if (sad > 0 || sadPlan > 0) {
                presenter.doShiftAccRowAdd('Sched. Acc. Main.', sad, sadPlan);
            }

            $.each(json.halls, function (index, value) {
                if (value['pd-scheduled-seconds'] > 0 || value['abu-seconds'] > 0 || value['banu-seconds'] > 0 || value['up-seconds'] > 0 || value['tune-seconds'] > 0 || value['bnr-seconds'] > 0) {
                    presenter.doShiftHallRowAdd(value);
                }
            });
            var cc = json['crew-chief'],
                    operators = json.operators,
                    team = cc;

            if (team != '' && operators != '') {
                if (operators.indexOf(team) === -1) {
                    team = team + ', ';
                } else {
                    team = '';
                }
            }

            team = team + operators;

            $team.html(team);
            $program.html(json['accelerator-program']);
            presenter.initializePlaceholders();
            needsSave = true;
            presenter.enableSlideEditor();

        });

        request.error(function (xhr, textStatus) {
            window.console && console.log('Unable to get shift info: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
            alert('Unable to get shift info');

        });

        request.always(function () {
            presenter.endRequest();

            if (needsSave) {
                /*Can't save during existing request*/
                presenter.saveSlide();
            }
        });
    } else {
        presenter.endRequest();
    }
};

presenter.updateWeekInfo = function () {

    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.startRequest();

    if (confirm('Are you sure you want to overwrite the week info (both tables)?')) {

        var needsSave = false,
                deliveryDate = presenter.dateFromIsoString(presenter.deliveryDate),
                startHour = '07',
                $acctbody = $("#viewport .accelerator-activities-table tbody"),
                $hallstbody = $("#viewport .beam-to-halls-table tbody");

        deliveryDate.setDate(deliveryDate.getDate() - 7); // We go from Wed (today) to last Wed
        var date = presenter.dateToIsoString(deliveryDate),
                weekStart = date + 'T' + startHour;

        var request = jQuery.ajax({
            url: "/btm/rest/week-summary",
            type: "GET",
            data: {
                'week-start': weekStart
            },
            dataType: "json"
        });

        request.done(function (json) {
            presenter.disableSlideEditor();
            $(".aloha-cleanme").remove();
            $acctbody.empty();
            $hallstbody.empty();

            var studies = json['acc-studies-seconds'],
                    studiesPlan = json['plan-studies-seconds'];
            presenter.doWeekAccRowAdd('Acc. Beam Studies', studies, studiesPlan);

            var restore = json['acc-restore-seconds'],
                    restorePlan = json['plan-restore-seconds'];
            presenter.doWeekAccRowAdd('Acc. Restoration', restore, restorePlan);

            var acc = json['acc-acc-seconds'],
                    accPlan = json['plan-acc-seconds'];
            presenter.doWeekAccRowAdd('Acc. Config. Change', acc, accPlan);

            var sad = json['acc-sad-seconds'],
                    sadPlan = json['plan-sad-seconds'];
            presenter.doWeekAccRowAdd('Sched. Acc. Main.', sad, sadPlan);

            $.each(json.halls, function (index, value) {
                presenter.doWeekHallRowAdd(value);
            });

            presenter.initializePlaceholders();
            needsSave = true;
            presenter.enableSlideEditor();

        });

        request.error(function (xhr, textStatus) {
            window.console && console.log('Unable to get week info: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
            alert('Unable to get week info');

        });

        request.always(function () {
            presenter.endRequest();

            if (needsSave) {
                /*Can't save during existing request*/
                presenter.saveSlide();
            }
        });
    } else {
        presenter.endRequest();
    }
};

presenter.updateWeekAccesses = function () {

    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.startRequest();

    if (confirm('Are you sure you want to overwrite the access table?')) {

        var needsSave = false,
                startDate = presenter.dateFromIsoString(presenter.deliveryDate),
                endDate = presenter.dateFromIsoString(presenter.deliveryDate),
                startHour = '07:00',
                $tbody = $("#viewport .access-times-table tbody");

        startDate.setDate(startDate.getDate() - 7); // We go from Wed (today) to last Wed
        var startDateStr = presenter.dateToIsoString(startDate),
                endDateStr = presenter.dateToIsoString(endDate),
                weekStart = startDateStr + ' ' + startHour,
                weekEnd = endDateStr + ' ' + startHour;

        var request = jQuery.ajax({
            url: "https://opsweb.acc.jlab.org/CSUEApps/PSSestamp/controlled_access_summary_json.php",
            type: "GET",
            data: {
                'start': weekStart,
                'end': weekEnd
            },
            jsonp: "jsonp",
            dataType: "jsonp"
        });

        request.done(function (json) {
            presenter.disableSlideEditor();
            $(".aloha-cleanme").remove();
            $tbody.empty();

            var recordReason, record;

            recordReason = 'PCC - Planned Configuration Change';
            record = json[recordReason];
            presenter.doWeekAccessRowAdd(recordReason, record);

            recordReason = 'Repair/Investigate';
            record = json[recordReason];
            presenter.doWeekAccessRowAdd(recordReason, record);

            recordReason = 'Opportunistic';
            record = json[recordReason];
            presenter.doWeekAccessRowAdd(recordReason, record);

            recordReason = 'Other';
            record = json[recordReason];
            presenter.doWeekAccessRowAdd(recordReason, record);


            presenter.sumTableColumns();
            presenter.initializePlaceholders();
            needsSave = true;
            presenter.enableSlideEditor();

        });

        request.error(function (xhr, textStatus) {
            window.console && console.log('Unable to get week accesses: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
            alert('Unable to get week accesses');

        });

        request.always(function () {
            presenter.endRequest();

            if (needsSave) {
                /*Can't save during existing request*/
                presenter.saveSlide();
            }
        });
    } else {
        presenter.endRequest();
    }
};

presenter.addEmptyRowAndReset = function () {
    presenter.disableSlideEditor();
    $(".aloha-cleanme").remove();
    presenter.doShiftHallRowAdd();
    presenter.initializePlaceholders();
    presenter.enableSlideEditor();
    presenter.saveSlide();
};

presenter.doShiftHallRowAdd = function (data) {
    var hall = '',
            program = '',
            scheduled = '',
            actual = '',
            abu = '',
            banu = '';

    if (data !== undefined) {
        hall = 'Hall ' + data.hall.toUpperCase();

        program = data['hall-program'];

        if (program !== '') {
            hall = hall + ' (' + program + ')';
        }

        scheduled = (data['pd-scheduled-seconds'] / 3600).toFixed(1);
        actual = ((data['up-seconds'] + data['bnr-seconds']) / 3600).toFixed(1);
        abu = (data['abu-seconds'] / 3600).toFixed(1);
        banu = (data['banu-seconds'] / 3600).toFixed(1);
    }

    var $tbody = $("#viewport .bta-table tbody");
    $tbody.append("<tr><td><span class=\"table-field editable noformat\">" + hall + "</span></td><td><span class=\"table-field editable noformat\">" + scheduled + "</span></td><td><span class=\"table-field editable noformat\">" + actual + "</span></td><td><span class=\"table-field editable noformat\">" + abu + "</span></td><td><span class=\"table-field editable noformat\">" + banu + "</span></td></tr>");
};

presenter.doShiftAccRowAdd = function (program, value, plan) {

    var $tbody = $("#viewport .bta-table tbody");
    $tbody.append("<tr><td><span class=\"table-field editable noformat\">" + program + "</span></td><td><span class=\"table-field editable noformat\">" + (plan / 3600).toFixed(1) + "</span></td><td><span class=\"table-field editable noformat\">" + (value / 3600).toFixed(1) + "</span></td><td><span class=\"table-field editable noformat\">" + "</span></td><td><span class=\"table-field editable noformat\">" + "</span></td></tr>");
};

presenter.doWeekHallRowAdd = function (data) {
    var hall = '',
            hall_sched_seconds = '',
            hall_sched = '',
            pd_scheduled = '',
            accAvail = '',
            abuPercent = '',
            hallAvail = '';

    if (data !== undefined) {
        hall = 'Hall ' + data.hall.toUpperCase();

        hall_sched_seconds = (data['er-seconds'] + data['pcc-seconds'] + data['ued-seconds']);
        hall_sched = (hall_sched_seconds / 3600).toFixed(1);
        pd_scheduled = (data['pd-scheduled-seconds'] / 3600).toFixed(1);

        if (hall_sched_seconds === 0.0) {
            accAvail = '0.0';
            abuPercent = '0.0';
            hallAvail = '0.0';
        } else {
            accAvail = ((data['abu-seconds'] + data['banu-seconds']) / (hall_sched_seconds - data['acc-seconds']) * 100).toFixed(1);
            abuPercent = (data['abu-seconds'] / hall_sched_seconds * 100).toFixed(1);
            hallAvail = ((data['er-seconds'] + data['pcc-seconds']) / hall_sched_seconds * 100).toFixed(1);
        }
    }

    var $tbody = $("#viewport .beam-to-halls-table tbody");
    $tbody.append("<tr><td><span class=\"table-field\">" + hall + "</span></td><td><span class=\"table-field editable noformat\">" + pd_scheduled + "</span></td><td><span class=\"table-field editable noformat\">" + hall_sched + "</span></td><td><span class=\"table-field editable noformat\">" + accAvail + "</span></td><td><span class=\"table-field editable noformat\">" + abuPercent + "</span></td><td><span class=\"table-field editable noformat\">" + hallAvail + "</span></td></tr>");
};

presenter.doWeekAccessRowAdd = function (reason, data) {
    var hall_a = '0.0',
            hall_b = '0.0',
            hall_c = '0.0',
            hall_d = '0.0',
            acc = '0.0';

    if (data !== undefined) {
        hall_a = (data['hall-a-seconds'] / 3600).toFixed(1);
        hall_b = (data['hall-b-seconds'] / 3600).toFixed(1);
        hall_c = (data['hall-c-seconds'] / 3600).toFixed(1);
        hall_d = (data['hall-d-seconds'] / 3600).toFixed(1);
        acc = (data['accelerator-seconds'] / 3600).toFixed(1);
    }

    var $tbody = $("#viewport .access-times-table tbody");
    $tbody.append("<tr class=\"sum-row\"><td><span class=\"table-field\">" + reason + "</span></td><td class=\"sum-data\"><span class=\"table-field editable noformat\">" + hall_a + "</span></td><td class=\"sum-data\"><span class=\"table-field editable noformat\">" + hall_b + "</span></td><td class=\"sum-data\"><span class=\"table-field editable noformat\">" + hall_c + "</span></td><td class=\"sum-data\"><span class=\"table-field editable noformat\">" + hall_d + "</span></td><td class=\"sum-data\"><span class=\"table-field editable noformat\">" + acc + "</span></td></tr>");
};

presenter.doWeekAccRowAdd = function (program, value, plan) {

    var $tbody = $("#viewport .accelerator-activities-table tbody");
    $tbody.append("<tr><td><span class=\"table-field\">" + program + "</span></td><td><span class=\"table-field editable noformat\">" + (plan / 3600).toFixed(1) + "</span></td><td><span class=\"table-field editable noformat\">" + (value / 3600).toFixed(1) + "</span></td></tr>");
};

presenter.removeRow = function () {
    if (confirm("Are you sure you want to remove the last row of the BTA table?")) {
        $("#viewport .bta-table tbody tr:last").remove();
        presenter.saveSlide();
    }
};



/* PD Functions */
presenter.sumTableColumns = function () {
    $("#viewport .sum-table").each(function () {
        var x = $(this).find(".total-row .total-data").length;
        var totals = [];

        while (x--) {
            totals[x] = 0;
        }

        $(this).find("tr.sum-row").each(function () {
            $(this).find("td.sum-data").each(function (i) {
                totals[i] += parseFloat($(this).find(".table-field").html());
            });
        });

        $(this).find("tr.total-row td.total-data").each(function (i) {
            var formattedNum = totals[i].toFixed(1);
            /*.replace(".00", "");
             formattedNum = formattedNum.replace(".10", ".1"); 
             formattedNum = formattedNum.replace(".20", ".2");
             formattedNum = formattedNum.replace(".30", ".3");
             formattedNum = formattedNum.replace(".40", ".4");
             formattedNum = formattedNum.replace(".50", ".5");
             formattedNum = formattedNum.replace(".60", ".6");
             formattedNum = formattedNum.replace(".70", ".7");
             formattedNum = formattedNum.replace(".80", ".8");
             formattedNum = formattedNum.replace(".90", ".9");*/
            $(this).find(".table-field").html(formattedNum);
        });
    });
};

presenter.sendToElog = function () {
    if (presenter.ajaxInProgress) {
        return;
    }

    presenter.ajaxInProgress = true;

    $("#send-to-elog-button").html($(".indicator16x16").clone().removeClass("indicator16x16"));

    var success = false, logId = null;

    var request = jQuery.ajax({
        url: presenter.ctx + "/send-to-elog",
        type: "POST",
        data: {
            presentationId: presenter.presentationId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            if ($(".code", data).text() === 'SESSION_EXPIRED' || $(".code", data).text() === 'NOT_AUTHORIZED') {
               window.open(presenter.ctx + "/relogin", '_blank').focus();
               //presenter.openPageInDialog(presenter.ctx + "/relogin", "Your session has expired");
            } else {
                alert('Unable to send to eLog: ' + $(".reason", data).html());
            }
        } else {
            logId = $(".logId", data).html();
            success = true;
        }
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to send to eLog: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to send to eLog');

    });

    request.always(function () {
        presenter.ajaxInProgress = false;
        $("#send-to-elog-button").html("Send To eLog");

        if (success) {
            $("#new-entry-url").attr("href", "https://" + presenter.logbookhost + "/entry/" + logId);
            $("#new-entry-url").text(logId);
            $("#success-dialog").dialog("open");
        }
    });
};

/* BIND EVENTS */
$(window).on("resize", function () {
    presenter.scaleSlide();
});

$(window).on("hashchange", function () {
    var slidenum = window.location.hash.substr(1);
    var slideItem = $("#slide-list > li:eq(" + (slidenum - 1) + ")");
    presenter.doSlideChange(slideItem);
});

$(document).on("focus", ".input-wrapper", function () {
    presenter.updatePlaceholder.call(this).addClass('focus');
});

$(document).on("blur", ".input-wrapper", function () {
    presenter.updatePlaceholder.call(this).removeClass('focus');
});

$(document).on("keyup", ".input-wrapper", function () {
    presenter.updatePlaceholder.call(this);
});

$(document).on("keydown", ".input-wrapper", function (e) {
    presenter.editableKeydown.call(this, e);
});

$(document).on("click", ".input-wrapper", function () {
    presenter.updatePlaceholder.call(this);
});

$(document).on("webkitfullscreenchange mozfullscreenchange fullscreenchange", function () {
    presenter.verifyFullScreenState();
});

$(document).on("click", "#update-shift-log-button", function () {
    presenter.updateShiftInfo.call(this);
});

$(document).on("click", "#update-week-log-button", function () {
    presenter.updateWeekInfo.call(this);
});

$(document).on("click", "#update-week-accesses-button", function () {
    presenter.updateWeekAccesses.call(this);
});

$(document).on("click", "#add-row-button", function () {
    presenter.addEmptyRowAndReset.call(this);
});

$(document).on("click", "#remove-row-button", function () {
    presenter.removeRow.call(this);
});

$(document).on("click", "#template-list > li", function () {
    $("#new-slide-dialog").dialog("close");
    presenter.newSlide($(this));
});

$(document).on("click", "#new-slide-button", function () {
    $("#new-slide-dialog").dialog("open");

    $(".lazy-iframe").each(function () {
        var src = $(this).attr("data-src"),
                parentElement = $(this).parent();
        $(this).remove();
        parentElement.append('<iframe src="' + src + '"></iframe>');
        presenter.updatePlaceholder.call(parentElement);
    });
});

$(document).on("click", "#import-shift-logs-button", function () {
    $("#import-shift-logs-dialog").dialog("open");
});

$(document).on("click", "#full-screen-button", function () {
    presenter.toggleFullScreen();
});

$(document).on("click", "#previous-button", function () {
    presenter.previousSlide();
});

$(document).on("click", "#next-button", function () {
    presenter.nextSlide();
});

$(document).on("keydown", function (e) {
    presenter.handleUserKeyPress(e);
});

$(document).on("click", "#do-login-button", function () {
    presenter.doLogin();
});

$(document).on("click", "#slide-list > li", function () {
    presenter.setSelected($(this));
});

$(document).on("click", ".delete", function (e) {
    presenter.deleteSlide.call(this);

    e.preventDefault(); /* Don't jump to anchor hashtag (#) */
    e.stopPropagation(); /* Don't select slide after deleting! */
});

$(document).on("click", "#image-chooser-button, .writable:not(.fullscreen) #viewport .slide:not(.synced-slide) .image-container", function (e) {
    document.getElementById("fileInput").click();
    e.preventDefault(); /*Don't allow default behavior which, in turn would cause hashchange event to fire*/
});

$(document).on("click", "#iframe-chooser-button, .writable:not(.fullscreen) #viewport .iframe-placeholder-cover", function (e) {
    presenter.updateEmbeddedFrame();
    e.preventDefault(); /*Don't allow default behavior which, in turn would cause hashchange event to fire*/
});

$(document).on("click", "#iframe-opener-button", function (e) {
    presenter.openEmbeddedFrameInNewWindow();
    e.preventDefault(); /*Don't allow default behavior which, in turn would cause hashchange event to fire*/
});

$(document).on("change", "#fileInput", function () {
    presenter.handleFiles(this.files);
});

$(document).on("click", "#help-button", function () {
    $("#helpDialog").dialog("open");
    /*window.open('http://opsntsrv.acc.jlab.org/ops_docs/online_document_files/MCC_online_files/shiftlog_examples.pdf');*/
});

$(document).on("click", "#send-to-elog-button", function () {
    if (confirm('Are you sure you want to send to the eLog?')) {
        presenter.sendToElog();
    }
});

$(document).on("click", ":not(.fullscreen).writable #viewport .slide:not(.synced-slide) .datepicker-target", function () {
    presenter.datepickertarget = this;
    $("#datepicker-dialog").dialog("option", "title", "Choose a " + $(this).closest("li").find(".kv-key").text());
    $("#datepicker-dialog").dialog("open");
});

/* EXECUTE AFTER DOM CONTENT LOADED */
$(document).ready(function () {


    /* EXECUTE INIT METHODS */
    presenter.initializeViewportScale();
    presenter.initializeNewSlideDialog();
    presenter.initializeDatepickerDialog();
    presenter.initializeHelpDialog();
    presenter.initializeImportShiftLogDialog();
    presenter.initializeElogSuccessDialog();
    presenter.initializePlaceholders();
    presenter.initializeSlideSorter();
    presenter.initializePollChecks();
    presenter.initializeHashState();
    presenter.initializeKeepAlive();
    presenter.initializeLaser();

    $(".datepicker").datepicker({
        dateFormat: 'DD MM d, yy',
        onSelect: function (dateText) {
            $(presenter.datepickertarget).text(dateText);
            $("#datepicker-dialog").dialog("close");
            presenter.saveSlide();
        }
    });

    $(".spinner").spinner({
        min: 1,
        max: 7
    });
});
