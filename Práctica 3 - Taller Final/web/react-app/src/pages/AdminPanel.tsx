import { useEffect, useState } from "react";

interface UserDto {
  id: number;
  nombre: string;
  email: string;
}

export default function AdminPanel() {
  const [users, setUsers] = useState<UserDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("/admin/users", { credentials: "include" })
      .then(r => r.json())
      .then(setUsers)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Cargando…</p>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Gestión de usuarios</h1>
      <table className="w-full text-left">
        <thead>
          <tr className="border-b"><th>ID</th><th>Nombre</th><th>Email</th><th></th></tr>
        </thead>
        <tbody>
          {users.map(u => (
            <tr key={u.id} className="border-b hover:bg-gray-50">
              <td>{u.id}</td>
              <td>{u.nombre}</td>
              <td>{u.email}</td>
              <td>
                <button
                  onClick={() => borrarUsuario(u.id)}
                  className="text-red-600 hover:underline"
                >
                  Borrar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  async function borrarUsuario(id: number) {
    if (!confirm("¿Seguro que quieres borrar este usuario?")) return;
    await fetch(`/admin/users/${id}`, { method: "DELETE", credentials: "include" });
    setUsers(prev => prev.filter(u => u.id !== id));
  }
}