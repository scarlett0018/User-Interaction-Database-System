# Project2_Report

## Overview

Project2_Report is a database-driven application designed to handle user interactions such as registration, post management, and interaction tracking. The application includes features for user verification, post creation, and detailed user actions with a focus on data integrity and transaction management.

## Features

- **User Registration**: Allows new users to register by entering their name and phone number. The system verifies if the user is already registered.
- **Post Management**: Enables users to create unique posts with specific details and store them in the database.
- **User Interactions**: Users can view, add, or delete interactions such as comments and likes on posts.
- **Data Integrity**: Uses SQL transactions to ensure data consistency and integrity during multiple SQL statement executions.
- **Validation**: Validates user input using regular expressions to ensure correct formats.

## Functions

- `seeReply(int integer)`: View replies to a specific post.
- `getTime()`: Get the current system time.
- `seeUserInfo()`: View basic user information.
- `seeAction()`: View user actions.
- `follow(String s)`: Follow a user.
- `check(String table, String column, String value)`: Check if a specific value exists in a specified table and column.
- `register()`: Register a new user.
- `generatePostID()`: Generate a unique post ID.
- `reply(Integer level)`: Reply to a post at a specified level.
- `action()`: Execute user actions.
- `publish()`: Publish a new post.

## SQL Operations

- **Prepared Statements**: Used for checking user existence, committing transactions, and rolling back if necessary.
- **Validation**: Validates user input using regular expressions.
- **ResultSet Handling**: Iterates through query results to handle multiple rows.

## Getting Started

### Prerequisites

- Java Development Kit (JDK)
- SQL Database (e.g., MySQL, PostgreSQL)

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/Project2_Report.git
   ```
2. Navigate to the project directory:
   ```sh
   cd Project2_Report
   ```
3. Set up the database:
   - Create a new database.
   - Execute the provided SQL script to set up the necessary tables.

### Running the Application

1. Compile the Java files:
   ```sh
   javac *.java
   ```
2. Run the main program:
   ```sh
   java Main
   ```

## Usage

- Register a new user by entering their name and phone number.
- Create and publish new posts with specific details.
- View and interact with posts by adding comments, likes, and replies.
- Check user information and track interactions.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any questions or feedback, please contact:

- Name: Your Name
- Email: your-email@example.com

---

Thank you for using Project2_Report!
