var serializationChanged = new Boolean(false);

// stop propagation of keypress on edit_title fields (SAK-19026)
// some get handled by a keyboard navigation function
$(document).ready(function(){
    $('.new_title').keypress(function(e){
        e.stopPropagation();
    });
})


function serialize(s)
{
	//kill the unsaved changes message
	if (navigator.userAgent.toLowerCase().indexOf("safari") != -1 && window != top) {
		top.onbeforeunload = function() { };
	}
	else {
		window.onbeforeunload = function() { };
	}
	
	var order = "";
    $('ul.sortable').children('li').each(function(idx, elm) {
      order += elm.id.split(':')[3] + " ";
    });       
	
	document.getElementById('content::state-init').value = order;
}

function doRemovePage(clickedLink) {
	var name = $(clickedLink).parent().parent().find(".item_label_box").text();
	var conf = confirm($("#del-message").text() + " " + name + "?");
	var theHref = $(clickedLink).attr('href');

	if (conf == true) {
		$("#call-results").fadeOut('400');
		$("#call-results").load(theHref, function() {
			var status = $(this).find("div#value").text();
			if (status == "pass") {
		    	var target = $(clickedLink).parent().parent();
				$(this).fadeIn('400');		
				$(target).slideUp('fast', $(target).remove());
			}
			else if (status == "fail") {
				$(this).fadeIn('400');
			}
			//TODO: refresh available pages, but don't mess up display message
			resetFrame();
	  	});
	}
	return conf;
}

// When we show a page, it is automatically enabled if it was not before
function doShowPage(clickedLink) {
		var theHref = $(clickedLink).attr('href');
		$("#call-results").fadeOut('10');
		$("#call-results").load(theHref, function() {
			var status = $("#call-results").find("#value").text();
			if (status == "pass") {
				$(clickedLink).parent().parent().find(".item_control.show_link").hide();
				$(clickedLink).parent().parent().find(".item_control.enable_link").hide();
				$(clickedLink).parent().parent().find(".item_control.disable_link").show();
				$(clickedLink).parent().parent().find(".item_control.hide_link").show();
				$("#call-results").fadeIn('400');
			}
			else if (status == "fail") {
				$("#call-results").fadeIn('400');
			}
	  	});
}

// When we hide a page - it has no effect on enable/disable
function doHidePage(clickedLink) {
		var theHref = $(clickedLink).attr('href');
		$("#call-results").fadeOut('10');
		$("#call-results").load(theHref, function() {
			var status = $("#call-results").find("#value").text();
			if (status == "pass") {
				$(clickedLink).parent().parent().find(".item_control.hide_link").hide();
				$(clickedLink).parent().parent().find(".item_control.show_link").show();
				$("#call-results").fadeIn('400');
			}
			else if (status == "fail") {
				$("#call-results").fadeIn('400');
			}
	  	});
}

// When we enable a page, we mark it visible automatically
function doEnablePage(clickedLink) {
		var theHref = $(clickedLink).attr('href');
		$("#call-results").fadeOut('10');
		$("#call-results").load(theHref, function() {
			var status = $("#call-results").find("#value").text();
			if (status == "pass") {
				$(clickedLink).parent().parent().find(".item_control.enable_link").hide();
				$(clickedLink).parent().parent().find(".item_control.disable_link").show();
				$(clickedLink).parent().parent().find(".item_control.show_link").hide();
				$(clickedLink).parent().parent().find(".item_control.hide_link").show();
				$("#call-results").fadeIn('400');
				$("#call-results").fadeIn('400');
			}
			else if (status == "fail") {
				$("#call-results").fadeIn('400');
			}
	  	});
}

// When we disable a page, it is also not visible
function doDisablePage(clickedLink) {
		var theHref = $(clickedLink).attr('href');
		$("#call-results").fadeOut('10');
		$("#call-results").load(theHref, function() {
			var status = $("#call-results").find("#value").text();
			if (status == "pass") {
				$(clickedLink).parent().parent().find(".item_control.disable_link").hide();
				$(clickedLink).parent().parent().find(".item_control.enable_link").show();
				$(clickedLink).parent().parent().find(".item_control.hide_link").hide();
				$(clickedLink).parent().parent().find(".item_control.show_link").show();
				$("#call-results").fadeIn('400');
			}
			else if (status == "fail") {
				$("#call-results").fadeIn('400');
			}
	  	});
}

function doEditPage(clickedLink) {
	var theHref = $(clickedLink).attr('href');
	$("#call-results").load(theHref, function() {
		var status = $("#call-results").find("#value").text();
		if (status == "pass") {
	    	var target = document.getElementById('content::page-row:' + $("#call-results").find("#pageId").text() + ':');
			$("#call-results").fadeIn('500');
					
		}
		else if (status == "fail") {
			$("#call-results").fadeIn('500');
		}
  	});
}

function showAddPage(clickedLink, init) {
	var theHref = $(clickedLink).attr('href');
	if (init) {
		$("#add-control").hide();
		$("#list-label").show();
		$(".tool_list").css("border", "1px solid #ccc");
	}
	$("#add-panel").fadeOut(1, $("#add-panel").load(theHref, function() {
		$("#call-results").fadeOut(200, function() {
			$("#call-results").html($("#add-panel").find("#message").html());
			$("#add-panel").fadeIn(200, $("#call-results").fadeIn(200, resetFrame()));
		});
	}));
}

function showEditPage(clickedLink) {
	li = $(clickedLink).parent().parent();
	$(li).find(".item_label_box").hide();
	$(li).find(".item_control_box").hide();
	$(li).find(".item_edit_box").fadeIn('normal');
	//$(li).removeClass("sortable_item");
	$(li).addClass("editable_item");
	$(li).unbind();
	resetFrame();
}

function doSaveEdit(clickedLink) {
	var theHref = $(clickedLink).attr('href');
	li = $(clickedLink).parent().parent();
	newTitle = $(li).find(".new_title");
	newConfig = $(li).find(".new_config");
	$("#call-results").load(clickedLink + "&newTitle=" + encodeURIComponent(newTitle.val()) + "&newConfig=" + encodeURIComponent(newConfig.val()), function() {

		var status = $("#call-results").find("#value").text();
		if (status == 'pass') {
			$(li).find(".item_edit_box").hide();
			newTitle = $("#call-results").find("#pageId strong").html();	
			$(li).find(".item_label_box").empty();
			$(li).find(".item_label_box").append(newTitle);
			$(li).find(".item_label_box").show();
			$(li).find(".item_label_box").attr("style", "display: inline");
			$(li).find(".item_control_box").show();
			$(li).find(".item_control_box").attr("style", "display: inline");
			$(li).addClass("sortable_item");
			$(li).removeClass("editable_item");
		}
  	});
}

function doCancelEdit(clickedLink) {
	li = $(clickedLink).parent().parent();
	$(li).find(".item_edit_box").hide();
	$(li).find(".item_label_box").show();
	$(li).find(".item_label_box").attr("style", "display: inline");
	$(li).find(".item_control_box").show();
	$(li).find(".item_control_box").attr("style", "display: inline");
	$(li).addClass("sortable_item");
	$(li).removeClass("editable_item");
	$(li).find(".new_title").val($(li).find(".item_label_box").text());
}

function checkReset() {
	var reset = confirm($("#reset-message").text());
	if (reset)
		return true;
	else
		return false;
}
				
function addTool(draggable, manual) {
	if (manual == true) {
		// we got fired via the add link not a drag and drop..
		//  so we need to manually add to the list
		$('#sort1').append(draggable);
	}
	$(draggable).attr("style", "");
	//force possitioning so IE displays this right
	$(draggable).position("static");
	$("#call-results").fadeOut('200');
	url = $(draggable).find(".tool_add_url").attr("href");
	oldId = $(draggable).id();
	$(draggable).empty();
	li = $(draggable);
	$("#call-results").load(url, function() {
		$(li).DraggableDestroy();
		$(li).id("content::" + $("#call-results").find("li").id());
		$(li).html($("#call-results").find("li").html());
		$(this).find("li").remove();
		$("#call-results").fadeIn('200', resetFrame());
	});
	return false;
}

