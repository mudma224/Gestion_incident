import { LifeBuoy } from "lucide-react";

function Header() {
  return (
    <header style={styles.header}>
      <div style={styles.logo}>
        <LifeBuoy size={22} />
        <span>Support Client</span>
      </div>
    </header>
  );
}

const styles = {
  header: {
    padding: "20px",
    background: "#0f172a",
    color: "white",
  },
  logo: {
    display: "flex",
    gap: "10px",
    alignItems: "center",
    fontWeight: "600",
  },
};

export default Header;