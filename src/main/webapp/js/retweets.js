;$(document).ready(function(){
	$('#tweets').tweetsLoader();
});

(function($){
	// プラグイン名
	var PLUGIN_NAME = "tweetsLoader";



//// 編集禁止↓ ///////////////////////////////////////////////////////
	var ImplementationClass = function(target, config) {
		var settings = $.extend({}, $[PLUGIN_NAME].defaults, config);
		var myself   = this;
		$(target).data(PLUGIN_NAME, myself);
//// 編集禁止↑ ///////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////////////////
// Private Properties
//
// 記述方法：
// var プロパティ名 = デフォルト値;
///////////////////////////////////////////////////////////////////////////

		var parameters;

///////////////////////////////////////////////////////////////////////////
// Public Properties
//
// 記述方法：
// myself.プロパティ名 = デフォルト値;
///////////////////////////////////////////////////////////////////////////

		myself.publicProp001 = "test";

///////////////////////////////////////////////////////////////////////////
// コンストラクタ
///////////////////////////////////////////////////////////////////////////

		/**
		 * constructor
		 */
		function _constructor_() {
			loadQuery();
			if(!parameters.account) {
				$(target).html("表示するアカウントが指定されていません。");
				return;
			}

			$.ajax({
				  type		: 'GET'
				, dataType	: 'json'
				, cache		: false
				, url		: "api/tweets/" + parameters.account
			}).done(function( data ) {
					if(data.tweets.length == 0) {
						return;
					}
					for (var i = 0; i < data.tweets.length; i++) {
						var id = data.tweets[i];
						var t = $('<blockquote class="twitter-tweet" lang="ja"><p>...</p><a href="https://twitter.com/' + data.screenName + '/status/' + id + '">...</a></blockquote>');
						$(target).append(t);

						var resDivID = "responses" + counter++;
						var d = $('<div/>');
						var a = $('<a href="#">返信を読む</a>');
						a.data("tweetID", id);
						a.data("divID",   resDivID);
						a.bind('click', function(event){
							event.preventDefault();
							var loader = $('#tweets').data("tweetsLoader");
							loader.loadResponses($(this).data("tweetID"), $(this).data("divID"));
						});
						d.append(a);

						var d2 = $('<div class="responses" style="display:none;"/>');
						d2.attr("id", resDivID);
						d.append(d2);
						$(target).append(d);
					}

					twttr.widgets.load();
			}).fail(function(data, status, err, callback) {
			});
		}
		var counter = 0;

///////////////////////////////////////////////////////////////////////////
/*// 編集禁止 ⇒//////////////////////////////*/ $.extend( myself, { ////


///////////////////////////////////////////////////////////////////////////
// Public Methods
//
// 記述方法：
// メソッド名 : function() {
//     実装
// }, ←このコンマを忘れないように
///////////////////////////////////////////////////////////////////////////

			/**
			 *
			 */
			loadResponses : function(tweetID, divID) {
				var $div = $("#" + divID);
				$div.empty();
				$div.show();
				$.ajax({
					  type		: 'GET'
					, dataType	: 'json'
					, cache		: false
					, url		: "api/responses/" + tweetID
				}).done(function( data ) {
						if(data.tweets.length == 0) {
							$div.hide();
							return;
						}
						for (var i = 0; i < data.tweets.length; i++) {
							var tweet = data.tweets[i];
							var id = tweet.ID;
							var t = $('<blockquote class="twitter-tweet" lang="ja"><p>...</p><a href="https://twitter.com/' + tweet.screenName + '/status/' + id + '">...</a></blockquote>');
							$div.append(t);
						}

						twttr.widgets.load();
				}).fail(function(data, status, err, callback) {
				});
			},



///////////////////////////////////////////////////////////////////////////
/*// 編集禁止 ⇒//////////*/ havingnopoint : "" }); _constructor_(); ////


///////////////////////////////////////////////////////////////////////////
// Private Methods
//
// 記述方法：
// function メソッド名() {
//     実装
// }
///////////////////////////////////////////////////////////////////////////

		/**
		 *
		 */
		function loadQuery() {
			parameters = {};
			if(document.location.search.length < 1) { return; }
			var query  = document.location.search.substring(1);
			var params = query.split('&');

			for (var i = 0; i < params.length; i++) {
				var element = params[i].split('=');
				var paramName = decodeURIComponent(element[0]);
				var paramValue = decodeURIComponent(element[1]);
				parameters[paramName] = paramValue;
			}
		}

///////////////////////////////////////////////////////////////////////////
/*// 編集禁止 ⇒//////////////////////////////*/ }; $[PLUGIN_NAME] = { ////


///////////////////////////////////////////////////////////////////////////
// Static Methods
//
// 記述方法：
// メソッド名 : function() {
//     実装
// }, ←このコンマを忘れないように
///////////////////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////////////////
// Default Settings
///////////////////////////////////////////////////////////////////////////

		defaults : {
			  defaultProp001 : 0
			, defaultProp002 : 0
		}

///////////////////////////////////////////////////////////////////////////


//// 編集禁止↓ ///////////////////////////////////////////////////////////
	};

	$.fn[PLUGIN_NAME] = function(config){
		return this.each(function(i){
			new ImplementationClass(this, config);
		});
	};

})(jQuery);
