import React, { useState, useEffect } from 'react';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireAuth?: boolean;
  redirectTo?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requireAuth = true, 
  redirectTo 
}) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await fetch('/auth/check', { 
          credentials: 'include' 
        });
        const data = await response.json();
        
        if (requireAuth && !data.authenticated) {
          window.location.href = redirectTo || '/welcome';
        } else if (!requireAuth && data.authenticated) {
          window.location.href = '/';
        } else {
          setIsAuthenticated(data.authenticated);
        }
      } catch (error) {
        if (requireAuth) {
          window.location.href = redirectTo || '/welcome';
        } else {
          setIsAuthenticated(false);
        }
      }
    };

    checkAuth();
  }, [requireAuth, redirectTo]);

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

  return <>{children}</>;
};

export default ProtectedRoute;