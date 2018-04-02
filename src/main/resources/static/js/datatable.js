$(document).ready( function () {
	 var table = $('#booksTable').DataTable({
			"sAjaxSource": "/books/get/all",
			"sAjaxDataProp": "",
			"order": [[ 0, "asc" ]],
			"aoColumns": [
			      { "data": "id", "render": function (data, type, full, meta) { return '<a href="/books/updateBook?id='+data +'">' + data + '</a>'; }},
			      { "data": "isbn" },
		          { "data" : "title"},
				  { "data": "author"},
				  { "data": "price"}
			]
	 })
	 
});


