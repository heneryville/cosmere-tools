# My ClojureScript Project

This is a ClojureScript project set up using Shadow CLJS. Below are the details for setting up and running the project.

## Project Structure

```
my-clojurescript-project
├── src
│   ├── main
│   │   └── my_clojurescript_project
│   │       └── core.cljs
├── public
│   ├── index.html
├── shadow-cljs.edn
├── package.json
└── README.md
```

## Setup Instructions

1. **Clone the repository:**
   ```
   git clone <repository-url>
   cd my-clojurescript-project
   ```

2. **Install dependencies:**
   Make sure you have Node.js and npm installed. Then run:
   ```
   npm install
   ```

3. **Start the development server:**
   Use Shadow CLJS to start the server:
   ```
   npx shadow-cljs watch app
   ```

4. **Open your browser:**
   Navigate to `http://localhost:3000` to view the application.

## Usage

- The main application logic can be found in `src/main/my_clojurescript_project/core.cljs`.
- Modify `public/index.html` to change the HTML structure or add additional assets.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.