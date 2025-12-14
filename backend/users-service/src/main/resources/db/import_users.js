db = db.getSiblingDB("users-db");

db.users.insertMany([
    {
        username: "aleksa",
        email: "aleksa@example.com",
        firstName: "Aleksa",
        lastName: "Velickovic",
        passwordHash: "$2a$08$uPQ8FCcgcR2VqykhXdKW2OdVY9GIarOtVQJf4iTVur53aegBFA.A6",
        role: "RK",
        active: true,
        lastPasswordReset: new Date("2025-01-01T12:00:00Z")
    },
    {
        username: "testuser",
        email: "test@example.com",
        firstName: "Test",
        lastName: "User",
        passwordHash: "$2a$10$eImiTXuWVxfM37uY4JANjQ==",
        role: "RK",
        active: true,
        lastPasswordReset: new Date("2025-02-01T09:30:00Z")
    },
    {
        username: "admin",
        email: "admin@example.com",
        firstName: "Admin",
        lastName: "Korisnik",
        passwordHash: "$2a$08$zkU3ED/TwsqjdBzMAJsLaumT.Lpy4Gky2KFXyE2.19dRh5WpYbSfO",
        role: "A",
        active: true,
        lastPasswordReset: new Date("2025-02-01T09:30:00Z")
    }
]);
