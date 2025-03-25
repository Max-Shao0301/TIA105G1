document.addEventListener("DOMContentLoaded", function () {
    // 模擬從後端獲取的寵物數據
    const petData = [
        { name: "小白", type: "狗", gender: "公", weight: "5kg", notes: "怕生" },
        { name: "咪咪", type: "貓", gender: "母", weight: "3.5kg", notes: "愛抓東西" },
        { name: "球球", type: "狗", gender: "公", weight: "2kg", notes: "喜歡吃餅乾" }
    ];

    function addPetToTable(pet) {
        const tableBody = document.querySelector("#petTable tbody");
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${pet.name}</td>
            <td>${pet.type}</td>
            <td>${pet.gender}</td>
            <td>${pet.weight}</td>
            <td>${pet.notes}</td>
        `;
        
        tableBody.appendChild(row);
    }

    // 填充寵物數據
    petData.forEach(addPetToTable);
});