document.addEventListener("DOMContentLoaded", function() {
    // Add event listener to the "Confirm" button
    var confirmButton = document.querySelector("button[type='submit'][form='profile-form']");
    if (confirmButton) {
        confirmButton.addEventListener("click", function(event) {
            event.preventDefault(); // Prevent form submission
            showSuccessMessage("Profile updated successfully!");
        });
    }

    // Add event listener to the "Save" button
    var saveButton = document.querySelector("button[type='submit'][form='notification-form']");
    if (saveButton) {
        saveButton.addEventListener("click", function(event) {
            event.preventDefault(); // Prevent form submission
            showSuccessMessage("Notification settings saved successfully!");
        });
    }
});

// Function to show success message
function showSuccessMessage(message) {
    alert(message);
}
