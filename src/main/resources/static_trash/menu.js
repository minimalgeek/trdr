// IIFE - Immediately Invoked Function Expression
(function(yourcode) {

	// The global jQuery object is passed as a parameter
	yourcode(window.jQuery, window, document);

}(function($, window, document) {

	// The $ is now locally scoped

	// Listen for the jQuery ready event on the document
	$(function() {

		$("ul#tabs li").click(function(e) {
			if (!$(this).hasClass("active")) {
				var tabNum = $(this).index();
				var nthChild = tabNum + 1;
				$("ul#tabs li.active").removeClass("active");
				$(this).addClass("active");
				$("ul#tab li.active").removeClass("active");
				$("ul#tab li:nth-child(" + nthChild + ")").addClass("active");
			}
		});

	});

	// The rest of the code goes here!

}));