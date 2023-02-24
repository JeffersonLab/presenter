jQuery = Aloha.jQuery;
$ = jQuery;

Aloha.ready( function() {                         
               
    Aloha.bind('aloha-editable-activated', function(){
        if($(Aloha.activeEditable.obj[0]).hasClass("noformat")) {
            $(".aloha-toolbar").hide();
        }
        
        var $tab1 = $("#tab-ui-container-1");
        
        $tab1.find(".aloha-ui-component-group:nth-child(3) button").attr("title", "Keep/Discard Pasted Content Formatting");        
        $tab1.find(".aloha-ui-component-group:nth-child(4) span:first-child button").attr("title", "Ordered List");
        $tab1.find(".aloha-ui-component-group:nth-child(4) span:last-child button").attr("title", "Unordered List");
        $tab1.find(".ui-buttonset").removeClass("ui-buttonset").find("button:last-child").remove();
        
        /*Show save button for those who can't live without it*/
        $("#save-button").removeAttr("disabled");
        $("#save-button").html("Save");        
    });
               
    // save all changes after leaving an editable
    Aloha.bind('aloha-editable-deactivated', function(){
        /*var content = Aloha.activeEditable.getContents();
        var contentId = Aloha.activeEditable.obj[0].id;
        var pageId = window.location.pathname;*/
        
        $("#save-button").attr("disabled", "disabled");
        
        presenter.sumTableColumns();
        presenter.saveSlide();
    });   
    
    presenter.enableSlideEditor();    
});


