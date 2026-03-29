const { onRequest } = require("firebase-functions/v2/https");
const { setGlobalOptions } = require("firebase-functions/v2");
const { defineString } = require("firebase-functions/params");
const { Configuration, PlaidApi, PlaidEnvironments } = require("plaid");

// Set global options for all v2 functions
setGlobalOptions({ region: "us-central1", memory: "256MiB" });

// Define parameters
const plaidClientId = defineString("PLAID_CLIENT_ID");
const plaidSecret = defineString("PLAID_SECRET");
const plaidEnv = defineString("PLAID_ENV", { default: "sandbox" });

let client;
function getPlaidClient() {
    if (!client) {
        const configuration = new Configuration({
            basePath: PlaidEnvironments[plaidEnv.value()],
            baseOptions: {
                headers: {
                    "PLAID-CLIENT-ID": plaidClientId.value(),
                    "PLAID-SECRET": plaidSecret.value(),
                },
            },
        });
        client = new PlaidApi(configuration);
    }
    return client;
}

exports.createLinkToken = onRequest(async (req, res) => {
    try {
        const plaidClient = getPlaidClient();
        const response = await plaidClient.linkTokenCreate({
            user: { client_user_id: req.query.user_id || "test-user" },
            client_name: "Finance App",
            products: ["auth", "transactions"],
            country_codes: ["US"],
            language: "en",
        });
        res.json(response.data);
    } catch (error) {
        console.error("Plaid Link Token Error:", error.response ? error.response.data : error.message);
        res.status(500).send(error.message);
    }
});

exports.exchangeToken = onRequest(async (req, res) => {
    const publicToken = req.body.public_token;
    if (!publicToken) {
        return res.status(400).send("Missing public_token");
    }

    try {
        const plaidClient = getPlaidClient();
        const response = await plaidClient.itemPublicTokenExchange({
            public_token: publicToken,
        });
        const accessToken = response.data.access_token;
        const itemId = response.data.item_id;

        res.json({
            access_token: accessToken,
            item_id: itemId
        });
    } catch (error) {
        console.error("Plaid Token Exchange Error:", error.response ? error.response.data : error.message);
        res.status(500).send(error.message);
    }
});
