import Sidebar from "./components/layout/Sidebar";
import Home from "./pages/Home";

function App() {
  return (
    <div className="app">
      <Sidebar />

      <div className="main">
        <Home />
      </div>
    </div>
  );
}

export default App;