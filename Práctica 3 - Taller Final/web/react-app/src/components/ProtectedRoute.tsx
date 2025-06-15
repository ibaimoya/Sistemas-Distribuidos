import React, { useState, useEffect } from 'react';
import { Navigate } from 'react-router-dom';


interface ProtectedRouteProps {
  children: React.ReactNode;
  requireAuth?: boolean;
  redirectTo?: string;
  adminOnly?: boolean;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requireAuth = true, 
  redirectTo,
  adminOnly = false
}) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [isAdmin, setIsAdmin] = useState<boolean>(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await fetch('/auth/check', { 
          credentials: 'include' 
        });
        const data = await response.json();
        
        setIsAuthenticated(data.authenticated);
        setIsAdmin(data.admin || false);
        
      } catch (error) {
        setIsAuthenticated(false);
        setIsAdmin(false);
      }
    };

    checkAuth();
  }, []);

  // Mientras se verifica la autenticación
  if (isAuthenticated === null) {
    return (
      <div className="min-h-screen bg-black text-white flex items-center justify-center">
        <div className="relative">
          <div className="w-16 h-16 border-4 border-[#1db954]/20 border-t-[#1db954] rounded-full animate-spin"></div>
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-8 h-8 bg-[#1db954]/20 rounded-full animate-pulse"></div>
          </div>
        </div>
      </div>
    );  
  }

  // Si requiere autenticación y no está autenticado
  if (requireAuth && !isAuthenticated) {
    return <Navigate to={redirectTo || '/welcome'} replace />;
  }

  // Si NO requiere autenticación (login/register) pero está autenticado
  if (!requireAuth && isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  // Si es solo para admin y no es admin
  if (adminOnly && !isAdmin) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;