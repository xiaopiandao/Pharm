const pretty = (data) => JSON.stringify(data, null, 2);

async function request(url, options = {}) {
  const resp = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  const text = await resp.text();
  let body;
  try { body = JSON.parse(text); } catch { body = text; }
  if (!resp.ok) throw body;
  return body;
}

function bindForms() {
  document.getElementById("drugForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const f = e.target;
    const payload = {
      code: f.code.value,
      name: f.name.value,
      spec: f.spec.value,
      unit: f.unit.value,
      barcode: f.barcode.value,
      lowStockThreshold: Number(f.lowStockThreshold.value),
    };
    try {
      const result = await request("/api/drugs", { method: "POST", body: JSON.stringify(payload) });
      document.getElementById("drugResult").textContent = pretty(result);
    } catch (err) {
      document.getElementById("drugResult").textContent = pretty(err);
    }
  });

  document.getElementById("scanBtn").addEventListener("click", async () => {
    const code = document.getElementById("scanCode").value.trim();
    if (!code) return;
    try {
      const result = await request(`/api/drugs/by-barcode/${encodeURIComponent(code)}`);
      document.getElementById("scanResult").textContent = pretty(result);
    } catch (err) {
      document.getElementById("scanResult").textContent = pretty(err);
    }
  });

  document.getElementById("inboundForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const f = e.target;
    const payload = {
      supplierName: f.supplierName.value,
      items: [{
        scanCode: f.scanCode.value,
        batchNo: f.batchNo.value,
        productionDate: f.productionDate.value,
        expiryDate: f.expiryDate.value,
        purchasePrice: Number(f.purchasePrice.value),
        quantity: Number(f.quantity.value),
      }],
    };
    try {
      const result = await request("/api/inbounds", { method: "POST", body: JSON.stringify(payload) });
      document.getElementById("inboundResult").textContent = pretty(result);
    } catch (err) {
      document.getElementById("inboundResult").textContent = pretty(err);
    }
  });

  document.getElementById("outboundForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const f = e.target;
    const payload = {
      receiver: f.receiver.value,
      items: [{ scanCode: f.scanCode.value, quantity: Number(f.quantity.value) }],
    };
    try {
      const result = await request("/api/outbounds", { method: "POST", body: JSON.stringify(payload) });
      document.getElementById("outboundResult").textContent = pretty(result);
    } catch (err) {
      document.getElementById("outboundResult").textContent = pretty(err);
    }
  });

  document.getElementById("lowStockBtn").addEventListener("click", async () => {
    try {
      const result = await request("/api/warnings/low-stock");
      document.getElementById("warnResult").textContent = pretty(result);
    } catch (err) {
      document.getElementById("warnResult").textContent = pretty(err);
    }
  });

  document.getElementById("expiringBtn").addEventListener("click", async () => {
    try {
      const result = await request("/api/warnings/expiring?days=90");
      document.getElementById("warnResult").textContent = pretty(result);
    } catch (err) {
      document.getElementById("warnResult").textContent = pretty(err);
    }
  });
}

bindForms();
