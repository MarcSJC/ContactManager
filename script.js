const URL = "http://localhost:8080/ContactManager/rest/";
let btnGet = document.querySelector("#btnGet");
let btnAdd = document.querySelector("#btnAdd");
let btnDel = document.querySelector("#btnDel");
let btnEdit = document.querySelector("#btnEdit");
let btnChange = document.querySelector("#btnChange");
let btnGetUsers = document.querySelector("#btnGetUsers");
let btnCloseModal = document.querySelector("#closeModal");
let firstNameField = document.querySelector("#firstname");
let lastNameField = document.querySelector("#lastname");
let changeForm = document.querySelector("#changeForm");
let newFirstNameField = document.querySelector("#newfirstname");
let newLastNameField = document.querySelector("#newlastname");
let array = [];
let table = document.querySelector("#table");

function displayTable(dispArray) {
	table.innerHTML = "<tr><th>Last Name</th><th>First Name</th></tr>";
	for (let i = 0 ; i < dispArray.length ; i++) {
	    table.innerHTML += "<tr><td>" + dispArray[i][0] + "</td><td>" + dispArray[i][1] + "</td></tr>";
	}
}

function reqUser(method, error) {
	let searchLastName = lastNameField.value;
	let searchFirstName = firstNameField.value;
	if (searchLastName === null || searchLastName === "" || searchFirstName === null || searchFirstName === "") {
		alert("Please input both last name and first name !");
	}
	else {
		let datatype;
		if (method === "GET") {
			datatype = "json";
		}
		else {
			datatype = "text";
		}
		let userURL = URL + "user/" + searchFirstName + "/" + searchLastName;
		$.ajax({
			type: method,
			url: userURL,
			dataType: datatype,
			success: function (data) {
				if (method === "GET") {
					array = [];
					array.push([data["Last Name"], data["First Name"]]);
				    displayTable(array);
				}
				else {
					alert(data);
				}
			},
			error: function (exception) {
				alert(error);
			}
		});
	}
}

function sendUser(method, error) {
	let searchLastName = lastNameField.value;
	let searchFirstName = firstNameField.value;
	if (searchLastName === null || searchLastName === "" || searchFirstName === null || searchFirstName === "") {
		alert("Please input both last name and first name !");
	}
	else {
		let userURL = URL + "user/";
		let newSearchLastName;
		let newSearchFirstName;
		let newdata;
		if ($('#changeForm').hasClass('show')) {
			userURL += searchFirstName + "/" + searchLastName;
			newSearchLastName = newLastNameField.value;
			newSearchFirstName = newFirstNameField.value;
			if (newSearchLastName === null || newSearchLastName === "" || newSearchFirstName === null || newSearchFirstName === "") {
				alert("Warning : You didn't input both last name and first name !");
			}
			newdata = JSON.stringify({"First Name" : newSearchFirstName, "Last Name" : newSearchLastName});
		}
		else {
			newdata = JSON.stringify({"First Name" : searchFirstName, "Last Name" : searchLastName});
		}
		$.ajax({
			type: method,
			url: userURL,
			dataType: "text",
			accept: 'application/json',
			contentType: "application/json",
			data: newdata,
			success: function (data) {
				alert(data)
			},
			error: function (exception) {
				alert(error);
			}
		});
	}
	$('#changeForm').modal('hide');
	newLastNameField.value = "";
	newFirstNameField.value = "";
}

function getUser() {
	let error = "This user doesn't exist in database, please check your input !\n(Remember that this application is case sensitive.)";
	reqUser("GET", error);
}

function addUser() {
	let error = "Unable to add this user to database, maybe he already exists ?";
	sendUser("POST", error);
}

function delUser() {
	let error = "Unable to delete this user, maybe he doesn't exist ?";
	reqUser("DELETE", error);
}

function editUser() {
	let error = "Unable to edit this user, maybe he doesn't exist ?";
	sendUser("PUT", error);
}

function getUsers() {
	$.ajax({
		type: "GET",
		url: URL + "users",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (data) {
			array = [];
			$.each(data, function() {
				array.push([this["Last Name"], this["First Name"]]);
		    });
		    array.sort();
		    displayTable(array);
		},
		error: function (exception) {
			console.log(exception);
		}
	});
}

btnGet.addEventListener("click", getUser);
btnAdd.addEventListener("click", addUser);
btnDel.addEventListener("click", delUser);
btnEdit.addEventListener("click", function() {
	let searchLastName = lastNameField.value;
	let searchFirstName = firstNameField.value;
	if (searchLastName === null || searchLastName === "" || searchFirstName === null || searchFirstName === "") {
		alert("Please input both last name and first name !");
	}
	else {
		$('#changeForm').modal('show');
	}
});
btnCloseModal.addEventListener("click", function() {
	newLastNameField.value = "";
	newFirstNameField.value = "";
});
btnChange.addEventListener("click", editUser);
btnGetUsers.addEventListener("click", getUsers);
