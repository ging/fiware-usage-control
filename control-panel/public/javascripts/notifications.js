$(function(){
	const zeroPadding = (d) => { if (d < 10) { return `0${d}`;} return d;};
	const getFullDate = (d) => { d.setMinutes(d.getMinutes() + d.getTimezoneOffset()); return `${zeroPadding(d.getDate())}-${zeroPadding(d.getMonth() + 1)}-${d.getFullYear()} ${zeroPadding(d.getHours())}:${zeroPadding(d.getMinutes())}`; };
	if (window.events) {
		const socket = io('/');
		socket.on("report", function(event){
			$('#eventsTable>tbody').prepend(`
				<tr class="animated flipInX">
				  <td>${event.userId}</td>
				  <td>${event.jobId}</td>
				  <th scope="row">${event.type}</td>
				  <td>${event.punishment}</td>
				  <td>${event.msg}</td>
				  <td>${getFullDate(event.createdAt || new Date())}</td>
				</tr>
			`)

		});
	}
})
