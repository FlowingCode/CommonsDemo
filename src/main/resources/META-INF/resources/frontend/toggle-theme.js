window.toggleTheme = {
	
	applyTheme: function(dark) {
		let theme = "";
		if(dark){
			theme = "dark";
		}
	    document.documentElement.setAttribute("theme", theme);
	},
	
}