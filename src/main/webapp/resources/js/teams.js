document.addEventListener("DOMContentLoaded", () => {
    const table = document.getElementById("teams-table");
    const tbody = table.querySelector("tbody");
    let draggedRow = null;

    // 1. Only allow dragging if the user explicitly clicked the handle
    tbody.addEventListener("mousedown", (e) => {
        const row = e.target.closest(".draggable-row");
        if (!row) return;

        if (e.target.classList.contains("drag-handle")) {
            row.setAttribute("draggable", "true");
        } else {
            row.setAttribute("draggable", "false");
        }
    });

    // 2. Drag Start: Track the row being dragged and apply a styling class
    tbody.addEventListener("dragstart", (e) => {
        draggedRow = e.target.closest(".draggable-row");
        if (!draggedRow) return;

        // Tiny timeout ensures the visual drag shadow is created before adding our class
        setTimeout(() => draggedRow.classList.add("dragging"), 0);
    });

    // 3. Drag Over: Determine where to insert the row based on cursor position
    tbody.addEventListener("dragover", (e) => {
        e.preventDefault(); // Required to allow dropping
        const targetRow = e.target.closest(".draggable-row");

        if (!targetRow || targetRow === draggedRow) return;

        // Determine if the cursor is past the halfway mark of the target row
        const bounding = targetRow.getBoundingClientRect();
        const offset = e.clientY - bounding.top;
        const isAfter = offset > bounding.height / 2;

        // Insert before or after the target row
        if (isAfter) {
            tbody.insertBefore(draggedRow, targetRow.nextSibling);
        } else {
            tbody.insertBefore(draggedRow, targetRow);
        }
    });

    // 4. Drag End: Cleanup classes
    tbody.addEventListener("dragend", () => {
        if (draggedRow) {
            draggedRow.classList.remove("dragging");
            draggedRow.setAttribute("draggable", "true"); // Reset to default
            draggedRow = null;
        }
    });
});