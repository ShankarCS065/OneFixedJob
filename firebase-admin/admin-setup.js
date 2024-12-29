// listAllUsers();
const fs = require('fs');

const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
        credential: admin.credential.cert(serviceAccount)
  });

// Function to set admin claim
async function setAdmin(uid) {
  await admin.auth().setCustomUserClaims(uid, { admin: true });
  console.log(`Admin claims set for user ${uid}`);
}

// Example usage
setAdmin('sMgENf4gOYQj7pqKczABwscr0Bx1');
